package com.kairos.scheduler.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.scheduler.service.scheduler_panel.ActivityToSchedulerQueueService;
import com.kairos.scheduler.service.scheduler_panel.UserToSchedulerQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.kairos.scheduler.constants.AppConstants.ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC;

@Component
public class ActivityToSchedulerJobQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(ActivityToSchedulerJobQueueListener.class);

    @Inject
    private ActivityToSchedulerQueueService activityToSchedulerQueueService;
    @Inject
    private ObjectMapper objectMapper;
    @KafkaListener(topics=ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC)
    public void processMessage(String message) {
        try {
            KairosScheduleJobDTO job = objectMapper.readValue(message,KairosScheduleJobDTO.class);
            activityToSchedulerQueueService.handleJob(job);

            logger.info("received content = '{}'", job.toString());
            //storage.put(content);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
