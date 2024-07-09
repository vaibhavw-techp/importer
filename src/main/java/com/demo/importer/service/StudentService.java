package com.demo.importer.service;

import com.demo.importer.dto.LogDisplayDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.exceptions.IllegalTokenException;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.repository.LogRepository;
import com.demo.importer.util.aws.KmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Value("${consumer.service.student.addition.url}")
    private String STUDENT_ADD_URL;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private LogMapper logMapper;
    @Autowired
    private KmsUtil kmsUtil;


    public List<LogDisplayDto> saveStudent(List<StudentAdditionDto> students) {
        List<LogDisplayDto> logDisplayDtos = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        String token = extractToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        for (StudentAdditionDto student : students) {
            try {
                encryptSensitiveAttributes(student);
                HttpEntity<StudentAdditionDto> requestEntity = new HttpEntity<>(student, headers);
                restTemplate.postForEntity(STUDENT_ADD_URL, requestEntity, StudentDisplayDto.class);
                handleResponse(student, HttpStatus.OK.value(), "Successful", logDisplayDtos);
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                int statusCode = ex.getStatusCode().value();
                handleResponse(student, statusCode, HttpStatus.valueOf(statusCode).getReasonPhrase(), logDisplayDtos);
            } catch (Exception ex) {
                handleResponse(student, HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error: " + ex.getMessage(), logDisplayDtos);
            }
        }

        return logDisplayDtos;
    }

    private void encryptSensitiveAttributes(StudentAdditionDto student) {
        String encryptedEmail = kmsUtil.encrypt(student.getEmail());
        student.setEmail(encryptedEmail);
    }

    private void handleResponse(StudentAdditionDto student, int statusCode, String status, List<LogDisplayDto> logDisplayDtos) {
        LogEntity logEntity = saveLog(student, statusCode, status);
        logDisplayDtos.add(logMapper.mapLogEntityToLogDisplayDto(logEntity));
    }

    private String extractToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String token = "";

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            token = jwt.getTokenValue();
        } else {
            throw new IllegalTokenException("Error While Extracting JWT Token From Principal Object");
        }
        return token;
    }

    LogEntity saveLog(StudentAdditionDto student, int statusCode, String status) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), status);
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
        return logEntity;
    }

}

