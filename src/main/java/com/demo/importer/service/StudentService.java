package com.demo.importer.service;


import com.demo.importer.dto.StudentAdditionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StudentService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void saveStudent(List<StudentAdditionDto> students) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("student", students);

        future.whenComplete((result,ex)->{
            if(ex == null) {
                System.out.println("Send message successfull, with current Offset: " + result.getRecordMetadata().offset());
            }
            else {
                System.out.println("Unable to send message!!" + ex.getMessage());
            }
        });
    }

}

