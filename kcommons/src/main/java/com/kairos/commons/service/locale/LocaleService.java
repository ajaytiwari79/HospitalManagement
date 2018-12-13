package com.kairos.commons.service.locale;

/**
 * Created by vipul on 10/5/18.
 */
public interface LocaleService {
     String getMessage(String code);

     String getMessage(String code, Object[] args);
}
