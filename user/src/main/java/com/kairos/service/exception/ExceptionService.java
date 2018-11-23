package com.kairos.service.exception;

import com.kairos.commons.custom_exception.DataNotMatchedException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.custom_exception.*;

import com.kairos.service.fls_visitour.exceptions.scheduler.FlsCallException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Created by vipul on 10/5/18.
 */
@Service
public class ExceptionService extends CommonsExceptionUtil {

    public void usernameNotFoundException(String message,Object... params) {
        throw new UsernameNotFoundException(convertMessage(message, params));
    }
    public void zipCodeNotFoundException(String message,Object... params) {
        throw new ZipCodeNotFound(convertMessage(message, params));
    }
    public void dataNotMatchedException(String message,Object... params) {
        throw new DataNotMatchedException(convertMessage(message, params));
    }
    public void unsupportedOperationException(String message,Object... params) {
        throw new UnsupportedOperationException(convertMessage(message, params));
    }
    public void runtimeException(String message,Object... params) {
        throw new RuntimeException(convertMessage(message, params));
    }
    public void flsCredentialException(String message,Object... params) {
        throw new FlsCredentialException(convertMessage(message, params));
    }

    public void exceptionWithoutConvertInRestClient(String message) {
        throw new com.kairos.commons.custom_exception.ActionNotPermittedException(message);
    }
    public void flsCallException(String message,Object... params) {
        throw new FlsCallException(convertMessage(message, params));
    }
    public void nullPointerException(String message,Object... params) {
        throw new NullPointerException(convertMessage(message, params));
    }
    public void unitNotFoundException(String message,Object... params) {
        throw new UnitNotFoundException(convertMessage(message, params));
    }

    public void invalidSize(String message,Object... params) {
        throw new InvalidSize(convertMessage(message, params));
    }
}