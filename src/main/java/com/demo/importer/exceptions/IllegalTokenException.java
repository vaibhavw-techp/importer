package com.demo.importer.exceptions;

public class IllegalTokenException extends RuntimeException {
    public IllegalTokenException(String resourceName) {
        super("You are not authorized to access this " + resourceName +" resource");
    }

}
