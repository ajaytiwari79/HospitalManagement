package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class DataNotMatchedException extends RuntimeException {

    public DataNotMatchedException(String message) {
        super(message);
    }

}
