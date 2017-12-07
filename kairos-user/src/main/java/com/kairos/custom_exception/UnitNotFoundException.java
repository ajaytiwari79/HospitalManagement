package com.kairos.custom_exception;

/**
 * Created by prabjot on 6/12/17.
 */
public class UnitNotFoundException extends RuntimeException {

    public UnitNotFoundException(String message) {
        super(message);
    }
}
