package com.kairos.commons.utils;

import com.kairos.commons.custom_exception.*;
import com.kairos.commons.service.locale.LocaleService;

import javax.inject.Inject;


/**
 * Created by vipul on 10/5/18.
 */
public class CommonsExceptionUtil {
    @Inject
    private LocaleService localeService;

    public String convertMessage(String message, Object... params) {
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

    public void actionNotPermittedException(String message, Object... params) {
        throw new ActionNotPermittedException(convertMessage(message, params));
    }


    public void invalidRequestException(String message, Object... params) {
        throw new InvalidRequestException(convertMessage(message, params));
    }

    public void duplicateDataException(String message, Object... params) {
        throw new DuplicateDataException(convertMessage(message, params));
    }
    public void internalError(String message, Object... params) {
        throw new InternalError(convertMessage(message, params));
    }

    public void illegalArgumentException(String message, Object... params) {
        throw new IllegalArgumentException(convertMessage(message, params));
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




    public void internalServerError(String message, Object... params) {
        throw new InternalError(convertMessage(message, params));
    }


}
