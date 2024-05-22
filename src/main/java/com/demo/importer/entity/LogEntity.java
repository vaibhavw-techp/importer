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
}
