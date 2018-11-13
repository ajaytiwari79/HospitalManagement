package com.planner.common.custum_exceptions;

public class RelationShipNotValidException extends RuntimeException{

    private String message;

    //=========================================================
    public RelationShipNotValidException(String message) {
        super(message);
        this.message = message;
    }
}
