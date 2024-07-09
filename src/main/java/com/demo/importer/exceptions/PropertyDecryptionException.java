package com.demo.importer.exceptions;

public class PropertyDecryptionException extends RuntimeException {

    public PropertyDecryptionException(String propertyName) {
        super("Incorrect Encryption format for Property " + propertyName);
    }

    public PropertyDecryptionException(String message,String propertyName) {
        super(message + propertyName);
    }
}
