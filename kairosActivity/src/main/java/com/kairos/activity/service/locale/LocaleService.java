package com.kairos.activity.service.locale;

/**
 * Created by vipul on 11/5/18.
 */
public interface LocaleService {
    public String getMessage(String code);

    public String getMessage(String code, Object[] args);
}
