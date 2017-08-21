package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class DataNotFoundByIdException extends RuntimeException{

    public String message;

    public DataNotFoundByIdException(String message) {
        this.message = message;
    }

}
