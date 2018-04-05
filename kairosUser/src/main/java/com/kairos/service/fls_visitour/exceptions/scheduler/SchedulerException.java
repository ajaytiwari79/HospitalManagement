package com.kairos.service.fls_visitour.exceptions.scheduler;

/**
 * An Exception implementation scheduler
 * Created by Rama.Shankar on 27/9/16.
 */
public class SchedulerException extends RuntimeException {

    public SchedulerException(String message) {
        super(message);
    }

    public SchedulerException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public SchedulerException(Throwable throwable) {
        super(throwable);
    }


}
