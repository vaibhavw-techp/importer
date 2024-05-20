package com.demo.importer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogEntity {
    private Long id;
    private String name;
    private String email;
    private int statusCode;
    private LocalDateTime timestamp;
    private String statusMessage;
    private String source;

    public LogEntity(String name, String email, int value, LocalDateTime timestamp, String statusMessage, String source) {
        this.name = name;
        this.email = email;
        this.statusCode = value;
        this.timestamp = timestamp;
        this.statusMessage = statusMessage;
        this.source = source;
    }
}
