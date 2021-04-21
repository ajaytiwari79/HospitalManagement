package com.kairos.scheduler.kafka.producer;


import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.scheduler.custom_exception.InvalidJobSubTypeException;
import com.kairos.scheduler.service.ActivityIntegrationService;
import com.kairos.scheduler.service.UserIntegrationService;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.kairos.scheduler.constants.AppConstants.*;

@Component
public class KafkaProducer {


    @Inject
    private SchedulerPanelService schedulerPanelService;
    @Inject private ActivityIntegrationService activityIntegrationService;
    @Inject private UserIntegrationService userIntegrationService;


    private static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void pushToQueue(KairosSchedulerExecutorDTO job) {
        logger.info("Pushing to ScheduleTOUserQueue----------->" + job.getId());
        String queueLabel;
        if (userSubTypes.contains(job.getJobSubType())) {
            queueLabel = SCHEDULER_TO_USER_QUEUE_TOPIC;
            userIntegrationService.exceuteScheduleJob(job);
        } else if (activitySubTypes.contains(job.getJobSubType())) {
            queueLabel = SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC;
            activityIntegrationService.exceuteScheduleJob(job);
        } else {
            logger.info("Invalid Job Type-----------> {}", job.getJobSubType());
        }
    }

}
