package com.kairos.scheduler.config;

import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * CreatedBy vipulpandey on 10/9/18
 **/
@Component
public class SchedulerBootstrapListener implements ApplicationListener<ApplicationReadyEvent> {
    @Inject
    private SchedulerPanelService schedulerPanelService;
    private static final Logger logger =LoggerFactory.getLogger(SchedulerBootstrapListener.class);
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        schedulerPanelService.initSchedulerPanels();
    }
}
