package com.kairos.service.exception_handler;

import com.kairos.custom_exception.ActionNotPermittedException;
import com.kairos.custom_exception.DataNotFoundByIdException;
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

    public void throwMessage(String type, String message, String source, Long id, String... arguments) {
        switch (type) {
            case NOT_FOUND:
                message = localeService.getMessage(message) + " " + id;
                throw new DataNotFoundByIdException(message);
            case NOT_PERMITTED:
                message = localeService.getMessage(message) + " " + id;
                throw new ActionNotPermittedException(message);
            default:
                throw new InternalError("unable to get ");
        }
    }

    public void dataNotFoundByIdException(String message, String source, Long id) {
        message = localeService.getMessage(message,source);
        throw new DataNotFoundByIdException(message);
    }
}
