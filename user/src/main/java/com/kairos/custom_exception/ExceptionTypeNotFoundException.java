package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class ExceptionTypeNotFoundException extends RuntimeException{

    public ExceptionTypeNotFoundException(String message) {
       super(message);
    }

}
