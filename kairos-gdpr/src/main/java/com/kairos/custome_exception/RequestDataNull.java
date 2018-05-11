package com.kairos.custome_exception;

public class RequestDataNull extends RuntimeException {
    public RequestDataNull(String message)
    {
        super(message);
    }
}
