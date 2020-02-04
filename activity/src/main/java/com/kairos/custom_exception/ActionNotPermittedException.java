package com.kairos.custom_exception;

/**
 * Created by vipul on 25/9/17.
 */
public class ActionNotPermittedException extends  RuntimeException {
    @Deprecated//Please use ExceptionService
    public ActionNotPermittedException(String message) {
        super(message);
    }
}