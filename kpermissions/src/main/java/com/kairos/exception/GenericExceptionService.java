package com.kairos.exception;

import com.kairos.commons.utils.CommonsExceptionUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Created by shiv on 01/04/19.
 */

@Service
public class GenericExceptionService extends CommonsExceptionUtil {

    public void internalServerError(String message, Object... params) {
        throw new InternalError(convertMessage(message, params));
    }

    public void illegalArgumentException(String message, Object... params) {
        throw new IllegalArgumentException(convertMessage(message, params));
    }

    public void usernameNotFoundException(String message, Object... params) {
        throw new UsernameNotFoundException(convertMessage(message, params));
    }

    public void unsupportedOperationException(String message, Object... params) {
        throw new UnsupportedOperationException(convertMessage(message, params));
    }

    public void runtimeException(String message, Object... params) {
        throw new RuntimeException(convertMessage(message, params));
    }
}
