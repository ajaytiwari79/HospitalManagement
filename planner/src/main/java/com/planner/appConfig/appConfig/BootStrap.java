package com.planner.appConfig.appConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BootStrap implements ApplicationListener<ContextRefreshedEvent> {

    private Logger log= LoggerFactory.getLogger(this.getClass());

    @Autowired IAppConfig iAppConfig;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        log.info(iAppConfig.getKairosBaseUrl());

    }
}
