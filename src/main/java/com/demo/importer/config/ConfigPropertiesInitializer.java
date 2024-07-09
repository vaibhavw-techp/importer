package com.demo.importer.config;

import com.demo.importer.config.aws.KMSUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Slf4j
public class ConfigPropertiesInitializer {

    @Autowired
    private KMSUtil kmsUtil;
    @Autowired
    private ConfigurableEnvironment environment;

    @Value("${spring.datasource.password}")
    private String encryptedDbPassword;
    @Value("${jwt.secret.key}")
    private String encryptedJwtSecretKey;

    private static final String SPRING_DATASOURCE_PASSWORD = "spring.datasource.password";
    private static final String JWT_SECRET_KEY = "jwt.secret.key";
    private static final String PROPERTY_SOURCE_NAME = "dev.importer.config";
    private static final Pattern ENC_PATTERN = Pattern.compile("^ENC\\((.+)\\)$");

    @PostConstruct
    public void initializeProperties() {
        log.info("Setting Properties AwsConfigPropertiesInitializer using PostConstruct Function");

        Map<String, Object> props = new HashMap<>();

        try {
            String decryptedDbPassword = handleEncryptedProperty(encryptedDbPassword);
            String decryptedJwtSecretKey = handleEncryptedProperty(encryptedJwtSecretKey);

            props.put(SPRING_DATASOURCE_PASSWORD, decryptedDbPassword);
            props.put(JWT_SECRET_KEY, decryptedJwtSecretKey);

            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
            log.info("Properties Decrypted and set successfully.");
        } catch (Exception e) {
            log.error("Error decrypting properties", e);
        }
    }

    private String handleEncryptedProperty(String inputProperty) {
        Matcher matcher = ENC_PATTERN.matcher(inputProperty);

        // If ENC(...) enclosed
        if (matcher.matches()) {
            String base64EncodedCipherText = matcher.group(1);
            return kmsUtil.decrypt(base64EncodedCipherText);
        } else {
            return inputProperty;
        }
    }

}
