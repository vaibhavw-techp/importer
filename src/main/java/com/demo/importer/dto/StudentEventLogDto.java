package com.demo.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentEventLogDto {
    private String name;
    private String email;
    private Integer age;
    private Long logId;
}
