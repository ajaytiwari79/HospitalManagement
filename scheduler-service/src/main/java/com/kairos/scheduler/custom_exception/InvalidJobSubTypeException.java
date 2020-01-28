package com.kairos.scheduler.custom_exception;

public class InvalidJobSubTypeException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public InvalidJobSubTypeException (String message) {
        super(message);
    }


}
