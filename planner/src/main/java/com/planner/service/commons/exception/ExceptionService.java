package com.planner.service.commons.exception;

import com.planner.common.custum_exceptions.DataNotFoundByIdException;
import com.planner.common.custum_exceptions.FieldAlreadyExistsException;
import com.planner.service.commons.locale.LocaleService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class ExceptionService {
    //~ Instance variables
    @Inject
    private LocaleService localeService;

    //=======================common=============================

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

    //============================Used throughout this planner module=================================

    public void dataNotFoundByIdException(String message, Object... params) {
        throw new DataNotFoundByIdException(convertMessage(message, params));
    }

    public void fieldAlreadyExistsException(String message, Object... params) {
        throw new FieldAlreadyExistsException(convertMessage(message, params));
    }
}
