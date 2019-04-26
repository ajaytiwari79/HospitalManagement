package com.kairos.scheduler.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.scheduler.service.scheduler_panel.ActivityToSchedulerQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ActivityToSchedulerJobQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(ActivityToSchedulerJobQueueListener.class);

    @Inject
    private ActivityToSchedulerQueueService activityToSchedulerQueueService;
    @Inject
    private ObjectMapper objectMapper;
    //Todo Yatharth uncomment this code when it kafka is ready
    // @KafkaListener(topics=ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC)
    public void processMessage(String message) {
        try {
            KairosScheduleJobDTO job = objectMapper.readValue(message,KairosScheduleJobDTO.class);
            logger.info("received content = '{}'", job.toString());
            activityToSchedulerQueueService.handleJob(job);
            //storage.put(content);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
