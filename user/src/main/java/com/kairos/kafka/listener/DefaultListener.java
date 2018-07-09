package com.kairos.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.QueueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DefaultListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultListener.class);

    @KafkaListener(topics="userSchedulerQueue")
    public void processMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            QueueDTO job = objectMapper.readValue(message, QueueDTO.class);
            logger.info("received content = '{}'", job.toString());
            //storage.put(content);
        }
        catch(Exception e) {

        }
    }
}
