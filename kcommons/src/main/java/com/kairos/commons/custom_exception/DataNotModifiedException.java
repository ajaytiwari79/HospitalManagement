package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 11/9/17.
 */
public class DataNotModifiedException extends RuntimeException{
    private final transient Object[] params;
    public DataNotModifiedException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}

