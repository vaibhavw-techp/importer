package com.demo.importer.service;

import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentEventLogDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.mapstruct.StudentMapper;
import com.demo.importer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private LogRepository logRepository;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private LogMapper logMapper;
    @Autowired
    private KafkaStudentProducerService kafkaStudentProducerService;

    public List<Long> saveStudents(List<StudentAdditionDto> students) {
        // Save Logs
        List<LogEntity> logEntities = students.stream()
                .map(student -> saveLog(student, 0, "NO TRANSFER"))
                .toList();

        // Convert StudentAdditionDto and LogId into StudentEventLogDto -> for Kafka transfer
        List<StudentEventLogDto> studentEventLogDtos = logEntities.stream()
                .map(logEntity -> {
                    StudentAdditionDto student = students.get(logEntities.indexOf(logEntity));
                    return studentMapper.mapStudentAdditionDtoAndLogIdToStudentEventLogDto(student, logEntity);
                })
                .toList();

        kafkaStudentProducerService.kafkaEventProducer(studentEventLogDtos);
        return logEntities.stream().map(LogEntity::getId).collect(Collectors.toList());
    }

    private LogEntity saveLog(StudentAdditionDto student, int statusCode, String transferState) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), transferState,"PENDING");
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
        System.out.println(logEntity);
        return logEntity;
    }

}
