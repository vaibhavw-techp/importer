package com.demo.importer.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsSecretsDto {
    private String jwtSecretKey;
    private String devDbPass;
}
