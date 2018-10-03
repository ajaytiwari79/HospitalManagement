package com.planner.service.commons.locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Locale;
@Service
public class LocaleServiceImpl implements LocaleService{

    @Inject
    private MessageSource messageSource;


    //=========================Non-Static===========================
    @Override
    public String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, null, locale);
    }
    @Override
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, locale);
    }
}
