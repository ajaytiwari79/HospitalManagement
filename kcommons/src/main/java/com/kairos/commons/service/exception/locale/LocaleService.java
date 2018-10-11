package com.kairos.commons.service.exception.locale;

/**
 * Created by vipul on 10/5/18.
 */
public interface LocaleService {
    public String getMessage(String code);

    public String getMessage(String code, Object[] args);
}
