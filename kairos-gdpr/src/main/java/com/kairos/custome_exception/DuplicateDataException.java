package com.kairos.custome_exception;

public class DuplicateDataException extends RuntimeException {

  public   DuplicateDataException(String message) {
      super(message);
  }

  public DuplicateDataException(String message,Throwable cause)
    {
        super(message,cause);
    }

}
