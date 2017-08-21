package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class DuplicateDataException extends RuntimeException{

    public String message;

    public DuplicateDataException(String message) {
        this.message = message;
    }

}
