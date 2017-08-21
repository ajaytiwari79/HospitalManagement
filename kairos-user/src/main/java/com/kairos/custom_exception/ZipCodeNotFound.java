package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class ZipCodeNotFound extends RuntimeException{

    public String message;

    public ZipCodeNotFound(String message) {
        this.message = message;
    }

}
