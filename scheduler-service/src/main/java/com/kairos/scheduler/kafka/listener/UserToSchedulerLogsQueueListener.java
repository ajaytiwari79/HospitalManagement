package com.kairos.scheduler.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.dto.KairosSchedulerLogsDTO;
import com.kairos.scheduler.service.scheduler_panel.SchedulerPanelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.kairos.scheduler.constants.AppConstants.USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC;

@Component
public class UserToSchedulerLogsQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(UserToSchedulerJobQueueListener.class);
    @Inject
    private SchedulerPanelService schedulerPanelService;

    @KafkaListener(topics=USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC)
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KairosSchedulerLogsDTO schedulerLogs = objectMapper.readValue(message,KairosSchedulerLogsDTO.class);
            schedulerPanelService.createJobScheduleDetails(schedulerLogs);
            logger.info("Job Details--------------------->"+schedulerLogs.getResult()+schedulerLogs.getStarted());
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);

        }
    }
}
