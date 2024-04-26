package com.demo.importer.service;


import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.mapstruct.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class StudentService {

    @Autowired
    private KafkaTemplate<String, StudentAdditionDto> kafkaTemplate;

    @Autowired
    private StudentMapper studentMapper;

    @Value("${topic.student}")
    private String studentTopic;

    public List<StudentDisplayDto> saveStudent(List<StudentAdditionDto> students) {

        for (StudentAdditionDto student : students) {
            CompletableFuture<SendResult<String, StudentAdditionDto>> future = kafkaTemplate.send(studentTopic, student);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Send message successful, with current Offset: " + result.getRecordMetadata().offset());
                } else {
                    System.out.println("Unable to send message!!" + ex.getMessage());
                }
            });
        }

        return studentMapper.mapStudentAdditionDtosToStudentDisplayDtos(students);
    }

}


