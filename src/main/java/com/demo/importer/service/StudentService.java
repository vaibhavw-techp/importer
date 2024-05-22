package com.demo.importer.service;

import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.dto.LogDisplayDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.mapstruct.StudentMapper;
import com.demo.importer.repository.LogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private LogMapper logMapper;

    @Value("${topic.student}")
    private String studentTopic;

    public List<LogDisplayDto> saveStudent(List<StudentAdditionDto> students) {
        List<LogDisplayDto> logDisplayDtos = new ArrayList<>();

        if (!isKafkaBrokerAvailable()) {
            students.forEach(student -> handleResponse(student, 503, "Kafka broker is not available",logDisplayDtos));
            return Collections.emptyList();
        }

        for (StudentAdditionDto student : students) {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(studentTopic, student);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    handleResponse(student, 200, "Send Message Successful", logDisplayDtos);
                } else {
                    handleResponse(student, 500, "An unexpected exception occurred: " + ex.getMessage(),logDisplayDtos);
                }
            });
        }

        return logDisplayDtos;
    }

    private boolean isKafkaBrokerAvailable() {
        try (Socket socket = new Socket("localhost", 9092)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void handleResponse(StudentAdditionDto student, int statusCode, String statusMessage, List<LogDisplayDto> logDisplayDtos) {
        LogEntity logEntity = saveLog(student, statusCode, statusMessage);
        logDisplayDtos.add(logMapper.mapLogEntityToLogDisplayDto(logEntity));
    }

    private LogEntity saveLog(StudentAdditionDto student, int statusCode, String statusMessage) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), statusMessage,"Producer");
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
        return logEntity;
    }

}
