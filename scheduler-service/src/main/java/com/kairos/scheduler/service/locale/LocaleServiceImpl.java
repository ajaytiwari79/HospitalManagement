package com.kairos.scheduler.service.locale;

import com.kairos.scheduler.utils.user_context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

/**
 * Created by vipul on 10/5/18.
 * Locale service implementation
 */
@Service
public class LocaleServiceImpl implements LocaleService{

    @Autowired
    private MessageSource messageSource;

    /*@Override
    public String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, null, locale);
    }
    @Override
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return this.messageSource.getMessage(code, args, locale);
    }*/
    @Override
    public String getMessage(String code) {
        Locale locale = new Locale(Optional.ofNullable(UserContext.getUserDetails().getLanguage()).orElse(""));
        return this.messageSource.getMessage(code, null, locale);
    }

    @Override
    public String getMessage(String code, Object[] args) {
        Locale locale = new Locale(Optional.ofNullable(UserContext.getUserDetails().getLanguage()).orElse(""));
        return this.messageSource.getMessage(code, args, locale);
    }

}
