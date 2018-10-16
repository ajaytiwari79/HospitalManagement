package com.kairos.scheduler.queue.producer;


import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerLogsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.inject.Inject;

import java.util.concurrent.ExecutionException;

import static com.kairos.constants.AppConstants.USER_TO_SCHEDULER_JOB_QUEUE_TOPIC;
import static com.kairos.constants.AppConstants.USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC;

@Component
public class KafkaProducer {


    @Inject
    private KafkaTemplate<Integer,KairosScheduleJobDTO> kafkaTemplateJobQueue;
    @Inject
    private KafkaTemplate<Integer,KairosSchedulerLogsDTO> kafkaTemplateLogsQueue;
    @Inject
    private static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);


    public void pushToJobQueue(KairosScheduleJobDTO job) throws Exception {

        try{
            kafkaTemplateJobQueue.send(USER_TO_SCHEDULER_JOB_QUEUE_TOPIC,job).get();

        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to register scheduled job, Please try again.");
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
    }

    public void pushToSchedulerLogsQueue(KairosSchedulerLogsDTO logs) {

        kafkaTemplateLogsQueue.send(USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC,logs);
    }

}
