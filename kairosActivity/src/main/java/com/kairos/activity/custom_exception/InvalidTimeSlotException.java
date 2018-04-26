package com.kairos.activity.custom_exception;

/**
 * Created by prabjot on 13/4/17.
 */
public class InvalidTimeSlotException extends RuntimeException {

    public InvalidTimeSlotException(String message) {
        super(message);
    }
}
