package com.kairos.ExceptionHandler;

public class DataNotFoundByIdException extends RuntimeException {
   public DataNotFoundByIdException(String message)
    {
super(message);
    }
}
