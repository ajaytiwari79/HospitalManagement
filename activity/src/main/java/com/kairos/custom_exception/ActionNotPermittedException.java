package com.kairos.custom_exception;

/**
 * Created by vipul on 25/9/17.
 */
public class ActionNotPermittedException extends  RuntimeException {
    public String message;

    public ActionNotPermittedException(String message) {
        super(message);
        this.message = message;
    }
}