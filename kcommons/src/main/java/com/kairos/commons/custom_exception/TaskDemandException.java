package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 4/4/17.
 */
public class TaskDemandException extends RuntimeException {
    private final transient Object[] params;
    public TaskDemandException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
