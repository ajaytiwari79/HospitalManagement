package com.kairos.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.dto.QueueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShcedulerToUserQueueListener {

    private static final Logger logger = LoggerFactory.getLogger(ShcedulerToUserQueueListener.class);

    @KafkaListener(topics="SchedulerToUserQueue")
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
