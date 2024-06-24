package com.demo.importer.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class AwsSecretsPropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSecretsPropertiesListener.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String AWS_SECRET_NAME = "intern/dev/importer";
    private static final String AWS_REGION = "eu-north-1";
    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String SECRET_KEY_DB_PASSWORD = "dev-db-password";
    private static final String JWT_SECRET_KEY = "jwt.secret.key";
    private static final String AWS_JWT_SECRET_KEY_ID = "jwt-secret";

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        String secretJson = getSecret();
        if (secretJson != null) {
            String dbPassword = getString(secretJson, SECRET_KEY_DB_PASSWORD);
            String jwtSecret = getString(secretJson, AWS_JWT_SECRET_KEY_ID);

            ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
            Properties props = new Properties();
            props.put(SPRING_DATASOURCE_PASSWORD, dbPassword);
            props.put(JWT_SECRET_KEY,jwtSecret);
            environment.getPropertySources().addFirst(new PropertiesPropertySource("aws.secrets.manager", props));
        } else {
            LOGGER.error("Failed to retrieve secrets from AWS Secrets Manager");
        }
    }

    private String getSecret() {
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                .withRegion(AWS_REGION)
                .build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(AWS_SECRET_NAME);

        try {
            GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
            return getSecretValueResult.getSecretString();
        } catch (Exception e) {
            LOGGER.error("Error fetching secret from AWS Secrets Manager: {}", e.getMessage());
            return null;
        }
    }

    private String getString(String json, String path) {
        try {
            JsonNode root = mapper.readTree(json);
            return root.path(path).asText();
        } catch (IOException e) {
            LOGGER.error("Error parsing secret JSON: {}", e.getMessage());
            return null;
        }
    }
}
