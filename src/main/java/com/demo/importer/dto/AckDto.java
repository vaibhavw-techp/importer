package com.demo.importer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AckDto {
    private Long logId;
    private String name;
    private String currentState;
}
