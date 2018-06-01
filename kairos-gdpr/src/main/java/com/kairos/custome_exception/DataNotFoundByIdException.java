package com.kairos.custome_exception;

public class DataNotFoundByIdException extends RuntimeException {
   public DataNotFoundByIdException(String message)
    {
super(message);
    }
}
