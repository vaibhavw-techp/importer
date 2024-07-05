package com.demo.importer.config.aws;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;

import java.util.Base64;
import java.util.Properties;

@Component
public class AwsEncryptedPropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Autowired
    private KmsClient kmsClient;

    @Value("${cmkKeyARN}")
    private String cmkKeyARN;

    private static final String SPRING_DATASOURCE_PASSWORD_DECRYPTED = "spring.datasource.password.decrypted";
    private static final String JWT_SECRET_KEY = "jwt.secret.key";

    @Value("${dev.importer.datasource.password.encrypted}")
    private String encryptedDbPassword;

    @Value("${dev.jwt.secret.key.encrypted}")
    private String encryptedJwtSecret;


    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        System.out.println(encryptedDbPassword);
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        String str = kmsDecrypt(encryptedDbPassword);
        System.out.println(str);
        Properties props = new Properties();
        props.put(SPRING_DATASOURCE_PASSWORD_DECRYPTED, );
        props.put(JWT_SECRET_KEY, decryptedJwtSecret);
        environment.getPropertySources().addFirst(new PropertiesPropertySource("spring.datasource.", props));
    }

    public String decryptSensitiveConfigProperties(String sensProperty) {
        return kmsDecrypt(sensProperty);
    }

    public String kmsDecrypt(String base64EncodedValue) {
        if (base64EncodedValue == null || base64EncodedValue.isEmpty()) {
            return ""; // Handle null or empty case gracefully
        }
        DecryptRequest decryptRequest = buildDecryptRequest( base64EncodedValue );
        DecryptResponse decryptResponse = this.kmsClient.decrypt(decryptRequest);
        String decryptTest = decryptResponse.plaintext().asUtf8String();

        return decryptTest;
    }

    private DecryptRequest buildDecryptRequest(String base64EncodedValue) {
        SdkBytes encryptBytes = SdkBytes.fromByteArray(Base64.getDecoder().decode(base64EncodedValue));
        DecryptRequest decryptRequest = DecryptRequest.builder().keyId(cmkKeyARN)
                .ciphertextBlob(encryptBytes).build();
        return decryptRequest;
    }
}
