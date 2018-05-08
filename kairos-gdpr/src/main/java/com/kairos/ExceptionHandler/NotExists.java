package com.kairos.ExceptionHandler;

public class NotExists extends RuntimeException {

public NotExists(String message)
{
    super(message);
}

}
