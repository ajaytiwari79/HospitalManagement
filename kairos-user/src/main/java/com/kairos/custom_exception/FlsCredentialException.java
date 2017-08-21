package com.kairos.custom_exception;

/**
 * Created by oodles on 5/4/17.
 */
public class FlsCredentialException extends RuntimeException {
    public String message;

    public FlsCredentialException(String message) {
        this.message = message;
    }
}
