package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 13/4/17.
 */
public class InvalidTimeSlotException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public InvalidTimeSlotException(String message) {
        super(message);
    }
}
