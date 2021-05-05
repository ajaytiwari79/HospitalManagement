package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
public class DataNotFoundByIdException extends RuntimeException {
    private Object[] params;
    public DataNotFoundByIdException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }

}
