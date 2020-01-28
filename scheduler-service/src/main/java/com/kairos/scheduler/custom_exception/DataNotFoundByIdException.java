package com.kairos.scheduler.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
public class DataNotFoundByIdException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public DataNotFoundByIdException(String message) {
        super(message);
    }

}
