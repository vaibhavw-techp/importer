package com.demo.importer.service;

import com.demo.importer.dto.AckDto;
import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentEventLogDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.mapstruct.StudentMapper;
import com.demo.importer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StudentService {

    @Autowired
    private KafkaTemplate<String, StudentEventLogDto> kafkaTemplate;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LogMapper logMapper;

    @Value("${topic.student}")
    private String studentTopic;

    List<Long> logIds = new ArrayList<>();

    public List<Long> saveStudents(List<StudentAdditionDto> students) {

        List<StudentEventLogDto> studentEventLogDtos = new ArrayList<>();

        for(StudentAdditionDto student: students) {
            LogEntity logEntity = saveLog(student, 0, "INITIAL STAGE");
            StudentEventLogDto studentEventLogDto = new StudentEventLogDto(student.getName(),student.getEmail(),student.getAge(),logEntity.getId());
            studentEventLogDtos.add(studentEventLogDto);
            System.out.println(logEntity.getId()  + " Search this!");
            logIds.add(logEntity.getId());
        }

        for (int i = 0; i < students.size(); i++) {
            StudentEventLogDto student = studentEventLogDtos.get(i);
            Long logId = logIds.get(i);

            CompletableFuture<SendResult<String, StudentEventLogDto>> future = kafkaTemplate.send(studentTopic, student);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logRepository.updateTransferStateAndStatusCode(logId, "SUCCESSFUL", 200);
                } else {
                    logRepository.updateTransferStateAndStatusCode(logId, "FAILED", 500);
                }
            });
        }

        return logIds;
    }

    private boolean isKafkaBrokerAvailable() {
        try (Socket socket = new Socket("localhost", 9092)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private LogEntity saveLog(StudentAdditionDto student, int statusCode, String transferState) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), transferState,"PENDING");
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
        System.out.println(logEntity);
        return logEntity;
    }

    @KafkaListener(topics = "logTopic", groupId = "g-1", containerFactory = "studentListener")
    public void receiveConsumerAcknowledgment(AckDto ackReceiveDto) {
        Long logId = ackReceiveDto.getLogId();;
        String currentState = ackReceiveDto.getCurrentState();

        if(currentState.equals("SAVED")) {
            logRepository.updateCurrentState(logId, currentState);
        }
        else {
            logRepository.updateCurrentState(logId, currentState);
        }

    }

}
