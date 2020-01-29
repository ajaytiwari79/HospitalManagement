package com.planner.common.custum_exceptions;

public class DataNotFoundByTypeException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public DataNotFoundByTypeException(String message) {
        super(message);
    }
}
