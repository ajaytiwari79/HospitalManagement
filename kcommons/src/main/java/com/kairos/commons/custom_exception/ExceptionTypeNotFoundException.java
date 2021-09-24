package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class ExceptionTypeNotFoundException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public ExceptionTypeNotFoundException(String message) {
        super(message);
    }

}
