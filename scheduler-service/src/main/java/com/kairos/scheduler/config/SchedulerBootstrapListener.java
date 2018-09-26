package com.kairos.scheduler.config;

import com.kairos.dto.scheduler.SchedulerPanelDTO;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.scheduler.constants.AppConstants.JOB_TO_CHECK_SICK_USER;

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
