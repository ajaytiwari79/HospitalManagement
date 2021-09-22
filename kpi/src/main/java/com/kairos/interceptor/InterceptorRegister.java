package com.kairos.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by prabjot on 21/11/16.
 */
public class InterceptorRegister implements WebMvcConfigurer {


    @Bean
    ExtractOrganizationAndUnitInfoInterceptor extractOrganizationAndUnitInfoInterceptor(){
        return new ExtractOrganizationAndUnitInfoInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(extractOrganizationAndUnitInfoInterceptor());
    }

}
