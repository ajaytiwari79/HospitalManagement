package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class DataNotMatchedException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public DataNotMatchedException(String message) {
        super(message);
    }

}
