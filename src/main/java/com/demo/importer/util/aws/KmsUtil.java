package com.demo.importer.util.aws;

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
public class KmsUtil {

    @Autowired
    private KmsClient kmsClient;

    @Value("${cmkKeyARN}")
    private String cmkKeyArn;

    public String encrypt(String plainText) {
        EncryptRequest encryptRequest = createEncryptRequest(plainText);
        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        SdkBytes cipherTextBytes =  encryptResponse.ciphertextBlob();
        byte[] base64EncodedCipherText = Base64.getEncoder().encode(cipherTextBytes.asByteArray());
        return new String( base64EncodedCipherText );
    }

    public String decrypt(String base64EncodedCipherText) {
        DecryptRequest decryptRequest = createDecryptRequest( base64EncodedCipherText );
        DecryptResponse decryptResponse = this.kmsClient.decrypt(decryptRequest);
        return decryptResponse.plaintext().asUtf8String();
    }

    private EncryptRequest createEncryptRequest(String plainText) {
        SdkBytes plainTextBytes= SdkBytes.fromUtf8String(plainText);
        return EncryptRequest.builder().keyId(cmkKeyArn)
                .plaintext(plainTextBytes).build();
    }

    private DecryptRequest createDecryptRequest(String base64EncodedCipherText) {
        SdkBytes encryptBytes = SdkBytes.fromByteArray(Base64.getDecoder().decode(base64EncodedCipherText));
        return DecryptRequest.builder()
                .keyId(cmkKeyArn)
                .ciphertextBlob(encryptBytes)
                .build();
    }
}