package com.kairos.scheduler.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.scheduler.service.scheduler_panel.UserToSchedulerQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class UserToSchedulerJobQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(UserToSchedulerJobQueueListener.class);

    @Inject
    private UserToSchedulerQueueService userToSchedulerQueueService;
    @Inject
    private ObjectMapper objectMapper;

    //Todo Yatharth uncomment this code when it kafka is ready
    //@KafkaListener(topics=USER_TO_SCHEDULER_JOB_QUEUE_TOPIC)
    public void processMessage(String message) {
        try {
            KairosScheduleJobDTO job = ObjectMapperUtils.jsonStringToObject(message,KairosScheduleJobDTO.class);
            userToSchedulerQueueService.handleJob(job);

            logger.info("received content = '{}'", job.toString());
            //storage.put(content);
        }
        catch(Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
}
