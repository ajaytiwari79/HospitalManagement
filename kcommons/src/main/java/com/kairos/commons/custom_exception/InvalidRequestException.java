package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 22/11/16.
 */
public class InvalidRequestException extends RuntimeException {
    private final transient Object[] params;
    public InvalidRequestException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
