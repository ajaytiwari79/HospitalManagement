package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class CitizenNotFoundException extends RuntimeException{

    public CitizenNotFoundException(String message) {
        super(message);
    }

}
