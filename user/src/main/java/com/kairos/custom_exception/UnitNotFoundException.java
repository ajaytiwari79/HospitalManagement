package com.kairos.custom_exception;

/**
 * Created by prabjot on 6/12/17.
 */
public class UnitNotFoundException extends RuntimeException {

    private Object[] params;
    public UnitNotFoundException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
