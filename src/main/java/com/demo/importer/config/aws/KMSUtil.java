package com.demo.importer.config.aws;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;

@Component
public class KMSUtil {

    @Autowired
    private KmsClient kmsClient;

    @Value("${cmkKeyARN}")
    private String cmkKeyARN;

    public String kmsEncrypt(String plainData) {
        EncryptRequest encryptRequest = buildEncryptRequest(plainData);
        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        SdkBytes cipherTextBytes =  encryptResponse.ciphertextBlob();
        byte[] base64EncodedValue = Base64.getEncoder().encode(cipherTextBytes.asByteArray());
        String responseBase64 = new String( base64EncodedValue );
        return responseBase64;
    }

    public String kmsDecrypt(String base64EncodedValue) {
        DecryptRequest decryptRequest = buildDecryptRequest( base64EncodedValue );
        DecryptResponse decryptResponse = this.kmsClient.decrypt(decryptRequest);
        String decryptTest = decryptResponse.plaintext().asUtf8String();
        return decryptTest;
    }

    private EncryptRequest buildEncryptRequest(String plainData) {
        SdkBytes plainTextBytes= SdkBytes.fromUtf8String(plainData);
        EncryptRequest encryptRequest = EncryptRequest.builder().keyId(cmkKeyARN)
                .plaintext(plainTextBytes).build();
        return encryptRequest;
    }

    private DecryptRequest buildDecryptRequest(String base64EncodedValue) {
        SdkBytes encryptBytes = SdkBytes.fromByteArray(Base64.getDecoder().decode(base64EncodedValue));
        DecryptRequest decryptRequest = DecryptRequest.builder().keyId(cmkKeyARN)
                .ciphertextBlob(encryptBytes).build();
        return decryptRequest;
    }
}