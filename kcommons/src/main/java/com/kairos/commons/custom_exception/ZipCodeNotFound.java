package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class ZipCodeNotFound extends RuntimeException{

    public ZipCodeNotFound(String message) {
        super(message);
    }

}
