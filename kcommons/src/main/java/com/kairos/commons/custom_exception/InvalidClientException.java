package com.kairos.commons.custom_exception;

/**
 * Created by prabjot on 11/7/17.
 */
public class InvalidClientException extends RuntimeException {
    private Object[] params;
    public InvalidClientException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
