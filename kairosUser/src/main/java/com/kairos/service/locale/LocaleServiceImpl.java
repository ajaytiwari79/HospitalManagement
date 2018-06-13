package com.kairos.service.locale;

import com.kairos.config.security.CurrentUserDetails;
import com.kairos.util.userContext.UserContext;
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
public class LocaleServiceImpl implements LocaleService{

    @Autowired
    private MessageSource messageSource;

    @Override
    public String getMessage(String code) {
        Locale locale = new Locale(UserContext.getUserDetails().getLanguage());
        return this.messageSource.getMessage(code, null, locale);
    }

    @Override
    public String getMessage(String code, Object[] args) {
        CurrentUserDetails u = UserContext.getUserDetails();
        Locale locale = new Locale(UserContext.getUserDetails().getLanguage());
        return this.messageSource.getMessage(code, args, locale);
    }

}
