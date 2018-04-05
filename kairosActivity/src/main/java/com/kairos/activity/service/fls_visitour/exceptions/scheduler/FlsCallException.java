package com.kairos.activity.service.fls_visitour.exceptions.scheduler;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
public class FlsCallException extends RuntimeException {

    public FlsCallException(String message) {
        super(message);
    }

    public FlsCallException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public FlsCallException(Throwable throwable) {
        super(throwable);
    }


}