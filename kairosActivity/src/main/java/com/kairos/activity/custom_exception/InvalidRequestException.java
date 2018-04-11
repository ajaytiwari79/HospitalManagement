package com.kairos.activity.custom_exception;

/**
 * Created by prabjot on 22/11/16.
 */
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message){
        super(message);
    }
}
