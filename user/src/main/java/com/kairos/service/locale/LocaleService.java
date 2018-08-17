package com.kairos.service.locale;

import java.util.Locale;

/**
 * Created by vipul on 10/5/18.
 */
public interface LocaleService {
    String getMessage(String code);

    String getMessage(String code, Object[] args);
}
