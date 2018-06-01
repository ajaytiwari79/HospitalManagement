package com.kairos.custome_exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message)
    {
        super(message);
    }
}
