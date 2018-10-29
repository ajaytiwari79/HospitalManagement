package com.planner.common.custum_exceptions;

public class DataNotFoundByTypeException extends RuntimeException{
    private String message;

    //=========================================================
    public DataNotFoundByTypeException(String message) {
        super(message);
        this.message = message;
    }
}
