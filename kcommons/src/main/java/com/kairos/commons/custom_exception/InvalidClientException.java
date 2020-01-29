package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 11/7/17.
 */
public class InvalidClientException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public InvalidClientException(String message) {
        super(message);
    }
}
