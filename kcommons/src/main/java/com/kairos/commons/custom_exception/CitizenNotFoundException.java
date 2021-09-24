package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class CitizenNotFoundException extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public CitizenNotFoundException(String message) {
        super(message);
    }

}
