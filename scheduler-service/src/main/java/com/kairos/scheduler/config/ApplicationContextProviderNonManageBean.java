package com.kairos.scheduler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @auther anil maurya
 */
@Component
public class ApplicationContextProviderNonManageBean implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextProviderNonManageBean.class);

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ApplicationContextProviderNonManageBean.context=context;

    }
    public static ApplicationContext  getApplicationContext(){
        return context;
    }

}
