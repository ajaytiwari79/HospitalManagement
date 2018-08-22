package com.kairos.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SpringContext implements ApplicationContextAware {


    @Inject
    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return applicationContext;
    }
}
