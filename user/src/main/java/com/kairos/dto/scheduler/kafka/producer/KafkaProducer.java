package com.kairos.dto.scheduler.kafka.producer;


import com.kairos.dto.scheduler.KairosScheduleJobDTO;
import com.kairos.dto.scheduler.KairosSchedulerLogsDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.kairos.constants.AppConstants.USER_TO_SCHEDULER_JOB_QUEUE_TOPIC;
import static com.kairos.constants.AppConstants.USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC;

@Component
public class KafkaProducer {


    @Inject
    private KafkaTemplate<Integer,KairosScheduleJobDTO> kafkaTemplateJobQueue;
    @Inject
    private KafkaTemplate<Integer,KairosSchedulerLogsDTO> kafkaTemplateLogsQueue;

    public void pushToJobQueue(KairosScheduleJobDTO job) {

      kafkaTemplateJobQueue.send(USER_TO_SCHEDULER_JOB_QUEUE_TOPIC,job);
    }

    public void pushToSchedulerLogsQueue(KairosSchedulerLogsDTO logs) {

        kafkaTemplateLogsQueue.send(USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC,logs);
    }

}
