package com.kairos.service.exception;

import com.kairos.commons.custom_exception.DataNotMatchedException;
import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.custom_exception.FlsCredentialException;
import com.kairos.custom_exception.InvalidSize;
import com.kairos.custom_exception.UnitNotFoundException;
import com.kairos.custom_exception.ZipCodeNotFound;
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
    public void dataNotMatchedException(String message,Object... params) {
        throw new DataNotMatchedException(message, params);
    }
    public void unsupportedOperationException(String message) {
        throw new UnsupportedOperationException(message);
    }
    public void exceptionWithoutConvertInRestClient(String message) {
        throw new com.kairos.commons.custom_exception.ActionNotPermittedException(message);
    }
    public void unitNotFoundException(String message,Object... params) {
        throw new UnitNotFoundException(message, params);
    }

    public void invalidSize(String message,Object... params) {
        throw new InvalidSize(message, params);
    }
}