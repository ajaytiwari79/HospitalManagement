package com.kairos.service.locale;

/**
 * Created by vipul on 11/5/18.
 */
public interface LocaleService {
    String getMessage(String code);

    String getMessage(String code, Object[] args);
}
