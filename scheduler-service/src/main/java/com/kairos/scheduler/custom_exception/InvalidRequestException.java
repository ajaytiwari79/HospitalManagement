package com.kairos.scheduler.custom_exception;

/**
 * Created by prabjot on 22/11/16.
 */
public class InvalidRequestException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public InvalidRequestException(String message){
        super(message);
    }
}
