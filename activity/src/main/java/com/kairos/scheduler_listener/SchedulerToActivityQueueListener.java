package com.kairos.scheduler_listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.service.mail.MailService;
import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class SchedulerToActivityQueueListener {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueListener.class);
    @Inject
    private JobQueueExecutor schedulerToActivityQueueService;
    @Inject private MailService mailService;

    //Todo Yatharth uncomment this code when it kafka is ready
    //@KafkaListener(topics=SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC)
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KairosSchedulerExecutorDTO job = objectMapper.readValue(message,KairosSchedulerExecutorDTO.class);
            logger.info("received content = '{}'", job.toString());
            schedulerToActivityQueueService.execute(job);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);
            mailService.sendMailToBackendOnException(e);

        }
    }
}
