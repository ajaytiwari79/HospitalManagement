package com.kairos.commons.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class ActionNotPermittedException extends RuntimeException {
    private Object[] params;
    public ActionNotPermittedException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
