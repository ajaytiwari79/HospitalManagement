package com.kairos.custom_exception;

public class JpaCustomDatabaseException extends RuntimeException {
    public JpaCustomDatabaseException(String message){
        super(message);
    }
}
