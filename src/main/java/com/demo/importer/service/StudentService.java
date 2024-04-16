package com.demo.importer.service;

import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.repository.LogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

    @Value("${post.student}")
    private String ADD_STUDENT_URL;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private LogMapper logMapper;

    @Transactional(propagation = Propagation.NEVER)
    public List<StudentDisplayDto>  saveDummyStudent2(List<StudentAdditionDto> studentAdditionDtos, String token) {
        return saveDummyStudent(studentAdditionDtos,token);
    }

    public List<StudentDisplayDto>  saveDummyStudent(List<StudentAdditionDto> studentAdditionDtos, String token) {
        return saveStudent(studentAdditionDtos,token);
    }


    public List<StudentDisplayDto> saveStudent(List<StudentAdditionDto> students, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        List<StudentDisplayDto> studentDisplayDtos = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (StudentAdditionDto student : students) {
            try {
                HttpEntity<StudentAdditionDto> requestEntity = new HttpEntity<>(student, headers);
                ResponseEntity<StudentDisplayDto> response = restTemplate.postForEntity(ADD_STUDENT_URL, requestEntity, StudentDisplayDto.class);
                studentDisplayDtos.add(response.getBody());
                saveLog(student, HttpStatus.OK.value(), "Successful");
            } catch (HttpClientErrorException ex) {
                saveLog(student, ex.getStatusCode().value(), extractErrorMessage(ex));
                throw ex;
            }
        }

        return studentDisplayDtos;
    }

    void saveLog(StudentAdditionDto student, int statusCode, String status) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), status);
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
    }


    private String extractErrorMessage(HttpClientErrorException ex) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = mapper.readValue(ex.getResponseBodyAsString(), new TypeReference<Map<String, Object>>() {});
            StringBuilder errorMessage = new StringBuilder();
            responseMap.forEach((key, value) -> {
                errorMessage.append(key).append(": ").append(value.toString()).append("; ");
            });
            return errorMessage.toString();
        } catch (IOException e) {
            return ex.getStatusText();
        }
    }


}
