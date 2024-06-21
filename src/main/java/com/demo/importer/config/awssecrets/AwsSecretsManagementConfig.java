package com.demo.importer.config.awssecrets;

 import com.demo.importer.dto.AwsSecretsDto;
 import com.fasterxml.jackson.databind.JsonNode;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import org.springframework.beans.factory.annotation.Value;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import software.amazon.awssdk.regions.Region;
 import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
 import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
 import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

 @Configuration
public class AwsSecretsManagementConfig {
     @Value("${aws.region}")
     private String awsRegion;

     @Value("${aws.secretsmanager.jwt.secret}")
     private String jwtSecretId;

     @Bean
     public AwsSecretsDto secret() throws Exception {
         SecretsManagerClient client = SecretsManagerClient.builder()
                 .region(Region.of(awsRegion))
                 .build();

         GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                 .secretId(jwtSecretId)
                 .build();

         GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
         String secretString = getSecretValueResponse.secretString();

         ObjectMapper objectMapper = new ObjectMapper();
         JsonNode secretJson = objectMapper.readTree(secretString);

         String jwtSecret = secretJson.get("jwt-secret").asText();
         String devDbPassword = secretJson.get("dev-db-password").asText();

         return new AwsSecretsDto(jwtSecret, devDbPassword);
     }
}
