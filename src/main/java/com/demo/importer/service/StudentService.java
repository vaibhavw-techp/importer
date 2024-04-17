package com.demo.importer.service;

import com.demo.importer.dto.LogDisplayDto;
import com.demo.importer.dto.StudentAdditionDto;
import com.demo.importer.dto.StudentDisplayDto;
import com.demo.importer.dto.LogAddtionDto;
import com.demo.importer.entity.LogEntity;
import com.demo.importer.mapstruct.LogMapper;
import com.demo.importer.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Value("${post.student}")
    private String ADD_STUDENT_URL;
    @Autowired
    private LogRepository logRepository;
    @Autowired
    private LogMapper logMapper;

    public List<LogDisplayDto> saveStudent(List<StudentAdditionDto> students) {
        List<LogDisplayDto> logDisplayDtos = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String token = jwt.getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        for (StudentAdditionDto student : students) {
            try {
                HttpEntity<StudentAdditionDto> requestEntity = new HttpEntity<>(student, headers);
                ResponseEntity<StudentDisplayDto> response = restTemplate.postForEntity(ADD_STUDENT_URL, requestEntity, StudentDisplayDto.class);
                LogEntity logEntity = saveLog(student, HttpStatus.OK.value(), "Successful");
                logDisplayDtos.add(logMapper.mapLogEntityToLogDisplayDto(logEntity));
            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                int statusCode = ex.getStatusCode().value();
                LogEntity logEntity = saveLog(student, statusCode, HttpStatus.valueOf(statusCode).getReasonPhrase());
                logDisplayDtos.add(logMapper.mapLogEntityToLogDisplayDto(logEntity));
            } catch (Exception ex) {
                int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
                String errorMessage = "Internal Server Error: " + ex.getMessage();
                LogEntity logEntity = saveLog(student, statusCode, errorMessage);
                logDisplayDtos.add(logMapper.mapLogEntityToLogDisplayDto(logEntity));
            }
        }

        return logDisplayDtos;
    }

    LogEntity saveLog(StudentAdditionDto student, int statusCode, String status) {
        LogAddtionDto logDto = new LogAddtionDto(student.getName(), student.getEmail(), statusCode, LocalDateTime.now(), status);
        LogEntity logEntity = logMapper.mapLogAdditionDtoToLogEntity(logDto);
        logRepository.saveStudentLog(logEntity);
        return logEntity;
    }

}

