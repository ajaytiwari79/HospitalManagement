package com.kairos.commons.custom_exception;


/*
* Pradeep singh rajawat
* date:5/2/2018
* HandleTimeTypeLink exception
* */
public class TimeTypeLinkedException extends RuntimeException {

    @Deprecated//Please use ExceptionService
    public TimeTypeLinkedException(String message){
        super(message);
    }
}
