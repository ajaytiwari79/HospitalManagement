package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 4/4/17.
 */
public class TaskDemandException extends RuntimeException {
    @Deprecated//Please use ExceptionService
    public TaskDemandException(String message) {
        super(message);
    }
}
