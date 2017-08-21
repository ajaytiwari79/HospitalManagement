package com.kairos.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class AddressNotVerifiedByTomTom extends RuntimeException{

    public String message;

    public AddressNotVerifiedByTomTom(String message) {
        this.message = message;
    }

}
