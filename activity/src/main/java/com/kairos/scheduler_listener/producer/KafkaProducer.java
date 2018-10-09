package com.kairos.scheduler_listener.producer;


import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.kairos.constants.AppConstants.ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC;
import static com.kairos.constants.AppConstants.ACTIVITY_TO_SCHEDULER_LOGS_QUEUE_TOPIC;

@Component
public class KafkaProducer {


    @Inject
    private KafkaTemplate<Integer,KairosScheduleJobDTO> kafkaTemplateJobQueue;
    @Inject
    private KafkaTemplate<Integer,KairosSchedulerLogsDTO> kafkaTemplateLogsQueue;

    public void pushToJobQueue(KairosScheduleJobDTO job) {

      kafkaTemplateJobQueue.send(ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC,job);
    }

    public void pushToSchedulerLogsQueue(KairosSchedulerLogsDTO logs) {

        kafkaTemplateLogsQueue.send(ACTIVITY_TO_SCHEDULER_LOGS_QUEUE_TOPIC,logs);
    }

}
