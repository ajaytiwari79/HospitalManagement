package com.kairos.activity.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class DuplicateDataException extends RuntimeException{

    public DuplicateDataException(String message) {
        super(message);
    }

}
