package com.kairos.scheduler_listener;

import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
import com.kairos.scheduler_listener.producer.KafkaProducer;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ActivityToSchedulerQueueService {

    @Inject
    private KafkaProducer kafkaProducer;

    public void pushToJobQueueForShiftReminder(List<KairosScheduleJobDTO> scheduleJobDTOS) {

        //kafkaProducer.pushToJobQueue(scheduledJob);
    }
}