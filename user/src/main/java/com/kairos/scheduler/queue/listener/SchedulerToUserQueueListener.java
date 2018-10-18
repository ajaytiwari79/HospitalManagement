package com.kairos.scheduler.queue.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;

import javax.inject.Inject;

import static com.kairos.constants.AppConstants.SCHEDULER_TO_USER_QUEUE_TOPIC;

@Component
public class SchedulerToUserQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerToUserQueueListener.class);
    @Inject
    private JobQueueExecutor schedulerToUserQueueService;

    @KafkaListener(topics=SCHEDULER_TO_USER_QUEUE_TOPIC)
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KairosSchedulerExecutorDTO job = objectMapper.readValue(message,KairosSchedulerExecutorDTO.class);
            logger.info("received content = '{}'", job.toString());
            schedulerToUserQueueService.execute(job);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);

        }
    }
}
