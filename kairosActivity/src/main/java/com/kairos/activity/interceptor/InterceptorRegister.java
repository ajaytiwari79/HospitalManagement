package com.kairos.activity.interceptor;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by prabjot on 21/11/16.
 */
public class InterceptorRegister extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ExtractOrganizationAndUnitInfoInterceptor());
    }

}
