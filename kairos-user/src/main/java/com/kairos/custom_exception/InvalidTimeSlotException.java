package com.kairos.custom_exception;

/**
 * Created by prabjot on 13/4/17.
 */
public class InvalidTimeSlotException extends RuntimeException {

    public String message;

    public InvalidTimeSlotException(String message) {
        this.message = message;
    }
}
