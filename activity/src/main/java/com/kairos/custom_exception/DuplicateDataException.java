package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class DuplicateDataException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public DuplicateDataException(String message) {
        super(message);
    }

}
