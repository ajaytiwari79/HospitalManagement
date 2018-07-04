package com.kairos.kafka.producer;


import com.kairos.dto.QueueDTO;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Inject;

public class KafkaProducer {

    @Inject
    private KafkaTemplate<Integer,QueueDTO> kafkaTemplate;

    public void pushToQueue(QueueDTO job) {

        kafkaTemplate.send("userSchedulerQueue",1,job);
    }

}
