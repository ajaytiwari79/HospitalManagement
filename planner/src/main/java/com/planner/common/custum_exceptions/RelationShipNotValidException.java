package com.planner.common.custum_exceptions;

public class RelationShipNotValidException extends RuntimeException{

    @Deprecated//Please use ExceptionService
    public RelationShipNotValidException(String message) {
        super(message);
    }
}
