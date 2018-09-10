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
        logger.info("Scheduler MS is started");
        List<DayOfWeek> days= Stream.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY,DayOfWeek.SATURDAY,DayOfWeek.SUNDAY).
                collect(Collectors.toList());
        List<String> selectedHours = Stream.of("23:00-23:59").collect(Collectors.toList());

        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(JOB_TO_CHECK_SICK_USER,true,0,8,days,null,
                selectedHours,null,null,JobType.FUNCTIONAL,JobSubType.USER_SICK,false,null,null);
        schedulerPanelService.createSchedulerPanel(null,schedulerPanelDTO,null);

    }
}
