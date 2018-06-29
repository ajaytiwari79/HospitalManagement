package com.kairos.service.locale;

import java.util.Locale;

/**
 * Created by vipul on 10/5/18.
 */
public interface LocaleService {
    public String getMessage(String code);

    public String getMessage(String code, Object[] args);
}
