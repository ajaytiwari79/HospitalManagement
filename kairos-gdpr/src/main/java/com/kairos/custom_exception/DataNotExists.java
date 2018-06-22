package com.kairos.custom_exception;

public class DataNotExists extends RuntimeException {

public DataNotExists(String message)
{
    super(message);
}

}
