package com.kairos.custom_exception;

public class DuplicateDataException extends RuntimeException {

  public   DuplicateDataException(String message) {
      super(message);
  }

  public DuplicateDataException(String message,Throwable cause)
    {
        super(message,cause);
    }

}
