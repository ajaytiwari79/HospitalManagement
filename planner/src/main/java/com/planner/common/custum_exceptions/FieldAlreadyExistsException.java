package com.planner.common.custum_exceptions;

public class FieldAlreadyExistsException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public FieldAlreadyExistsException(String message) {
        super(message);
    }
}
