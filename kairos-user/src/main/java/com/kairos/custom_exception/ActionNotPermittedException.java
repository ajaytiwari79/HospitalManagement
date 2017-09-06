package com.kairos.custom_exception;

/**
 * Created by vipul on 5/9/17.
 */
public class ActionNotPermittedException extends  RuntimeException {
    public String message;

    public ActionNotPermittedException(String message) {
        this.message = message;
    }
}
