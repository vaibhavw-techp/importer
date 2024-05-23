package com.demo.importer.service;

import com.demo.importer.dto.AckDto;
import com.demo.importer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaLogAckService {

    @Autowired
    private LogRepository logRepository;

    @KafkaListener(topics = "logTopic", groupId = "student-data-acknowledgment", containerFactory = "studentListener")
    public void receiveConsumerAcknowledgment(AckDto ackReceiveDto) {
        logRepository.updateCurrentState(ackReceiveDto.getLogId(), ackReceiveDto.getCurrentState());
    }
}
