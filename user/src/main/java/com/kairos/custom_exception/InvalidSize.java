package com.kairos.custom_exception;

public class InvalidSize extends RuntimeException  {

    public InvalidSize(String message) {
        super(message);
    }
}
