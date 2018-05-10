package com.kairos.custom_exception;

import com.kairos.service.locale.LocaleService;

import javax.inject.Inject;

/**
 * Created by vipul on 5/9/17.
 */
public class ActionNotPermittedException extends RuntimeException {
    @Inject
    private LocaleService localeService;
    public ActionNotPermittedException(String message) {
        super(message);
    }


}
