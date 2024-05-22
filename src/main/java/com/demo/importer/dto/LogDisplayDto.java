package com.demo.importer.dto;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogDisplayDto {
    private Long logId;
    private String name;
    private String email;
    private LocalDateTime timestamp;
    private int statusCode;
    private String transferState;
    private String currentState;
}
