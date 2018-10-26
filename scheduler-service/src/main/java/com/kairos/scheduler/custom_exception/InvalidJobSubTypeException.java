package com.kairos.scheduler.custom_exception;

public class InvalidJobSubTypeException extends RuntimeException{
    public InvalidJobSubTypeException (String message) {
        super(message);
    }


}
