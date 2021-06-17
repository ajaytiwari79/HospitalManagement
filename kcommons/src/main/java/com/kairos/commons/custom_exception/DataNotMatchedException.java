package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class DataNotMatchedException extends RuntimeException {
    private final transient Object[] params;
    public DataNotMatchedException(String message,Object... params) {
        super(message);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

}
