package com.kairos.scheduler.kafka.listener;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserToSchedulerLogsQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(UserToSchedulerLogsQueueListener.class);
    @Inject
    private SchedulerPanelService schedulerPanelService;

    //Todo Yatharth uncomment this code when it kafka is ready
    //@KafkaListener(topics=USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC)
    public void processMessage(String message) {
        try {
            KairosSchedulerLogsDTO schedulerLogs = ObjectMapperUtils.jsonStringToObject(message,KairosSchedulerLogsDTO.class);
            schedulerPanelService.createJobScheduleDetails(schedulerLogs);
            logger.info("Job Details--------------------->"+schedulerLogs.getResult()+schedulerLogs.getStartedDate());
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);

        }
    }
}
