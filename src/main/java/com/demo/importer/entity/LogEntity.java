package com.demo.importer.entity;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class LogEntity {
    private Long id;
    private String name;
    private String email;
    private int statusCode;
    private LocalDateTime timestamp;
    private String statusMessage;


    public LogEntity(String name, String email, int value, LocalDateTime timestamp, String statusMessage) {
        this.name = name;
        this.email = email;
        this.statusCode = value;
        this.timestamp = timestamp;
        this.statusMessage = statusMessage;
    }
}
