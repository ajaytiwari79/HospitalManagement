package com.kairos.config.security;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by neuron on 26/5/17.
 */
public class CustomAuthException extends AuthenticationException {

    public CustomAuthException(String msg, Throwable t) {
        super(msg, t);
    }

    public CustomAuthException(String msg) {
        super(msg);
    }
}
