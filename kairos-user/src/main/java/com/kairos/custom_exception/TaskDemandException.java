package com.kairos.custom_exception;

/**
 * Created by oodles on 4/4/17.
 */
public class TaskDemandException extends RuntimeException {

    public String message;

    public TaskDemandException(String message) {
        this.message = message;
    }
}
