package com.kairos.service.exception_handler;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.service.locale.LocaleService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.kairos.constants.ResponseConstants.NOT_FOUND;
import static com.kairos.constants.ResponseConstants.NOT_PERMITTED;


/**
 * Created by vipul on 10/5/18.
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

}
