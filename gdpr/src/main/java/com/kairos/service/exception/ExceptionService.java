package com.kairos.service.exception;

import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.custom_exception.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Created by vipul on 10/5/18.
 */

@Service
public class ExceptionService extends CommonsExceptionUtil {


    public void metaDataLinkedWithAssetException(String message, Object... params) {
        throw new MetaDataLinkedWithAssetException(convertMessage(message, params));
    }
    public void metaDataLinkedWithProcessingActivityException(String message, Object... params) {
        throw new MetaDataLinkedWithProcessingActivityException(convertMessage(message, params));
    }

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
