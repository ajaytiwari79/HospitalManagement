package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 8/9/17.
 */
public class DataNotFoundException extends RuntimeException {

    public DataNotFoundException(String message) {
        super(message);
    }
}
