package com.kairos.custom_exception;

public class InvalidSize extends RuntimeException  {

    private Object[] params;
    public InvalidSize(String message,Object... params) {
        super(message);
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }
}
