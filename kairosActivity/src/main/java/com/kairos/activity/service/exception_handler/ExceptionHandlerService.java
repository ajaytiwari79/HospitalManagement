package com.kairos.activity.service.exception_handler;

import com.kairos.activity.custom_exception.ActionNotPermittedException;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.custom_exception.DuplicateDataException;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.service.locale.LocaleService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by vipul on 11/5/18.
 */
@Service
public class ExceptionHandlerService {
    @Inject
    private LocaleService localeService;

    public void dataNotFoundByIdException(String message, Object... source) {
        source[0] = localeService.getMessage(source[0].toString());
        message = localeService.getMessage(message, source);
        throw new DataNotFoundByIdException(message);
    }

    public void actionNotPermittedException(String message, Object... source) {
        source[0] = localeService.getMessage(source[0].toString());
        message = localeService.getMessage(message, source);
        throw new ActionNotPermittedException(message);
    }

    public void duplicateDataException(String message, Object... source) {
        source[0] = localeService.getMessage(source[0].toString());
        message = localeService.getMessage(message, source);
        throw new DuplicateDataException(message);
    }

    public void invalidRequestException(String message, Object... source) {
        source[0] = localeService.getMessage(source[0].toString());
        message = localeService.getMessage(message, source);
        throw new InvalidRequestException(message);
    }

}
