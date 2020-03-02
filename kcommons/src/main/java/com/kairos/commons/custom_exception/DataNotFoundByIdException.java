package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
public class DataNotFoundByIdException extends RuntimeException {
    public DataNotFoundByIdException(String message) {
        super(message);
    }

}
