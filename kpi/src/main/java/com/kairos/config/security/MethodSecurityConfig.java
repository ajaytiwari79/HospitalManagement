package com.kairos.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    private ApplicationContext context;
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomOAuth2WebSecurityExpressionHandler customOAuth2WebSecurityExpressionHandler= new CustomOAuth2WebSecurityExpressionHandler();
        customOAuth2WebSecurityExpressionHandler.setApplicationContext(context);
        return customOAuth2WebSecurityExpressionHandler;
    }
}

