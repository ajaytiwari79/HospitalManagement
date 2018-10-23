package com.planner.common.custum_exceptions;

/**
 * @author mohit
 * @date 3-10-2018
 */
public class DataNotFoundByIdException extends RuntimeException {
    private String message;

    //=========================================================
    public DataNotFoundByIdException(String message) {
        super(message);
        this.message = message;
    }
}
