package com.kairos.commons.custom_exception;


/*
* Pradeep singh rajawat
* date:5/2/2018
* HandleTimeTypeLink exception
* */
public class TimeTypeLinkedException extends RuntimeException {

    private final transient Object[] params;
    public TimeTypeLinkedException(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }
}
