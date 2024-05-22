package com.demo.importer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataService {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    public String sendMessage(String message, int partition) {
        kafkaTemplate.send("temp-topic",partition,null,message);
        return "SUCCESS";
    }


}
