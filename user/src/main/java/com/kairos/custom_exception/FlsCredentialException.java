package com.kairos.custom_exception;

/**
 * Created by oodles on 5/4/17.
 */
public class FlsCredentialException extends RuntimeException {

    private Object[] params;
    public FlsCredentialException(String message,Object... params) {
        super(message);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }
}
