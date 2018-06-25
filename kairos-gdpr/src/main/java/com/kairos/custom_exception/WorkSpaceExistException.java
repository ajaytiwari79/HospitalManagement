package com.kairos.custom_exception;

public class WorkSpaceExistException extends RuntimeException {
    public WorkSpaceExistException(String message)
    {
        super(message);
    }
    public WorkSpaceExistException(String message, Throwable cause)
    {
        super(message,cause);

    }
}
