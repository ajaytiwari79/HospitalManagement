package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */

public class CitizenNotFoundException extends RuntimeException{

    public String message;

    public CitizenNotFoundException(String message) {
        this.message = message;
    }

}
