package com.kairos.ExceptionHandler;

public class DuplicateDataException extends RuntimeException {

  public   DuplicateDataException(String message)
    {
        super(message);
    }
    public DuplicateDataException(String message,Throwable cause)
    {
        super(message,cause);
    }

}
