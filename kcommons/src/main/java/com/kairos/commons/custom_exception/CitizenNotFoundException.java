package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class CitizenNotFoundException extends RuntimeException{
    private final transient Object[] params;
    public CitizenNotFoundException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
