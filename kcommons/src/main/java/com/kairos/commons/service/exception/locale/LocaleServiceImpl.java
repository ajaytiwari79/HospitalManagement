package com.kairos.commons.service.exception.locale;

import com.kairos.commons.service.exception.locale.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by vipul on 10/5/18.
 * Locale service implementation
 */
@Service
public class LocaleServiceImpl implements LocaleService {

    @Autowired
    private MessageSource messageSource;

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
    /*@Override
    public String getMessage(String code) {
        Locale locale = new Locale(Optional.ofNullable(UserContext.getUserDetails().getLanguage()).orElse(""));
        return this.messageSource.getMessage(code, null, locale);
    }

    @Override
    public String getMessage(String code, Object[] args) {
        Locale locale = new Locale(Optional.ofNullable(UserContext.getUserDetails().getLanguage()).orElse(""));
        return this.messageSource.getMessage(code, args, locale);
    }*/

}
