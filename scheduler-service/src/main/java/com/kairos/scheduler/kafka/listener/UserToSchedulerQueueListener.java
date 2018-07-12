package com.kairos.scheduler.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserToSchedulerQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(UserToSchedulerQueueListener.class);

    @KafkaListener(topics="UserToSchedulerQueue")
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            KairosSchedulerExecutorDTO job = objectMapper.readValue(message,KairosSchedulerExecutorDTO.class);
            logger.info("received content = '{}'", job.toString());
            //storage.put(content);
        }
        catch(Exception e) {

        }
    }
}
