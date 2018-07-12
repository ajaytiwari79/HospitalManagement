package com.kairos.scheduler.kafka.listener;

import com.kairos.dto.QueueDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;

public class DefaultKafkaMessageListener implements MessageListener<Integer,QueueDTO> {

public final static Logger logger = LoggerFactory.getLogger(DefaultKafkaMessageListener.class);


    public void onMessage(ConsumerRecord<Integer,QueueDTO> message) {

        logger.info("received: "+ message);


    }

}


