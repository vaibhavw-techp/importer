package com.demo.importer.service;

import com.demo.importer.dto.StudentEventLogDto;
import com.demo.importer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaStudentProducerService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private KafkaTemplate<String,StudentEventLogDto> kafkaTemplate;

    @Value("${kafka.producer.topic.student}")
    private String studentTopic;

    public void kafkaEventProducer(List<StudentEventLogDto> studentEventLogDtos) {
        // Kafka transmission - whenComplete
        studentEventLogDtos.forEach(studentEventLogDto -> {
            CompletableFuture<SendResult<String, StudentEventLogDto>> future = kafkaTemplate.send(studentTopic, studentEventLogDto);
            future.whenComplete((result, ex) -> {
                String transferState = (ex == null) ? "SUCCESSFUL" : "FAILED";
                int statusCode = (ex == null) ? 200 : 500;
                logRepository.updateTransferStateAndStatusCode(studentEventLogDto.getLogId(), transferState, statusCode);
            });
        });
    }

}
