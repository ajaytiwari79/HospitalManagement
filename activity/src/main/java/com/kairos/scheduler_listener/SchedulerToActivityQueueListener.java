package com.kairos.scheduler_listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.queue.kafka.JobQueueExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import javax.inject.Inject;

import static com.kairos.constants.AppConstants.SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC;

@Component
public class SchedulerToActivityQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueListener.class);
    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;

    @KafkaListener(topics=SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC)
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KairosSchedulerExecutorDTO job = objectMapper.readValue(message,KairosSchedulerExecutorDTO.class);
            logger.info("received content = '{}'", job.toString());
            schedulerToActivityQueueService.execute(job);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);

        }
    }
}
