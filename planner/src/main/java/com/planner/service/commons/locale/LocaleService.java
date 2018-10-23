package com.planner.service.commons.locale;

/**
 * @author mohit
 * @date 3-10-2018
 */
public interface LocaleService {

     String getMessage(String code);
     String getMessage(String code, Object[] args);
}
