package com.kairos.custome_exception;

public class DataNotExists extends RuntimeException {

public DataNotExists(String message)
{
    super(message);
}

}
