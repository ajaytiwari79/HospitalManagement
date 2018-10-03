package com.planner.common.custum_exceptions;

public class FieldAlreadyExistsException extends RuntimeException{
    private String message;

    //=========================================================
    public FieldAlreadyExistsException(String message) {
        super(message);
        this.message = message;
    }
}
