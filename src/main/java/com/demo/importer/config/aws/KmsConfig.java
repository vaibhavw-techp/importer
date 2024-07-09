package com.demo.importer.config.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;


@Configuration
public class KmsConfig {

    @Value("${aws.region}")
    private String appRegion;

    @Bean
    KmsClient kmsClient() {
        return KmsClient.builder().region(Region.of(appRegion)).build();
    }
}
