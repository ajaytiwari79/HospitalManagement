package com.kairos.commons.custom_exception;

/**
 * Created by oodles on 8/2/17.
 */
    public class AddressNotVerifiedByTomTom extends RuntimeException{
    private Object[] params;
    public AddressNotVerifiedByTomTom(String message,Object... params) {
        super(message);
        this.params = params;
    }
    public Object[] getParams() {
        return params;
    }

}
