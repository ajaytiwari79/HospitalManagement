package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 11/9/17.
 */
public class DataNotModifiedException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public DataNotModifiedException(String message) {

        super(message);
    }
}

