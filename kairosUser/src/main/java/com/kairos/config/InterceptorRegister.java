package com.kairos.config;

import com.kairos.config.interceptor.ExtractOrganizationAndUnitInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.inject.Inject;

/**
 * Created by prabjot on 21/11/16.
 */
@Configuration
public class InterceptorRegister extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new ExtractOrganizationAndUnitInfoInterceptor());
    }

}
