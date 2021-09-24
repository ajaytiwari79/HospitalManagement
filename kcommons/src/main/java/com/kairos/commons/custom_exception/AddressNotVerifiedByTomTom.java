package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class AddressNotVerifiedByTomTom extends RuntimeException{
    @Deprecated//Please use ExceptionService
    public AddressNotVerifiedByTomTom(String message) {
        super(message);
    }

}
