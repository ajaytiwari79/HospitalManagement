package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 5/4/17.
 */
public class FlsCredentialException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public FlsCredentialException(String message) {
        super(message);
    }
}
