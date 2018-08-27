package com.kairos.service.exception;

import com.kairos.custom_exception.*;
import com.kairos.service.locale.LocaleService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;


/**
 * Created by vipul on 10/5/18.
 */

@Service
public class ExceptionService {
    @Inject
    private LocaleService localeService;

    private String convertMessage(String message, Object... params) {
        for (int i = 0; i < params.length; i++) {
            try {
                params[i] = localeService.getMessage(params[i].toString());
            } catch (Exception e) {
                // intentionally left empty
            }

        }
        return localeService.getMessage(message, params);
    }

    public void dataNotFoundByIdException(String message, Object... params) {
        throw new DataNotFoundByIdException(convertMessage(message, params));
    }

    public void duplicateDataException(String message, Object... params) {
        throw new DuplicateDataException(convertMessage(message, params));
    }

    public void invalidRequestException(String message, Object... params) {
        throw new InvalidRequestException(convertMessage(message, params));
    }

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
