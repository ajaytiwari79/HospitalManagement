package com.kairos.config;

import com.kairos.config.interceptor.ExtractOrganizationAndUnitInfoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by prabjot on 21/11/16.
 */
@Configuration
public class InterceptorRegister implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(extractOrganizationAndUnitInfoInterceptor());
    }


    @Bean
    ExtractOrganizationAndUnitInfoInterceptor extractOrganizationAndUnitInfoInterceptor(){
        return new ExtractOrganizationAndUnitInfoInterceptor();
    }

}
