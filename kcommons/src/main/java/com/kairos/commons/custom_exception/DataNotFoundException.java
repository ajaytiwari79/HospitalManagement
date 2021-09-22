package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 8/9/17.
 */
public class DataNotFoundException extends RuntimeException {
    private final transient Object[] params;
    public DataNotFoundException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
