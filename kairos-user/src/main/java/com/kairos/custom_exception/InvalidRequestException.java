package com.kairos.custom_exception;

/**
 * Created by prabjot on 22/11/16.
 */
public class InvalidRequestException extends RuntimeException {
    private String message;

    public InvalidRequestException(String message) {
        super(message);
    }
}
