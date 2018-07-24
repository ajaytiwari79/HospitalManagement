package com.kairos.scheduler.service.scheduler_panel;

import com.kairos.dto.KairosScheduleJobDTO;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.enums.scheduler.JobType;
import com.kairos.scheduler.kafka.producer.KafkaProducer;
import com.kairos.scheduler.persistence.model.scheduler_panel.SchedulerPanel;
import com.kairos.scheduler.persistence.repository.SchedulerPanelRepository;
import com.kairos.scheduler.service.MongoBaseService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerPanelServiceTemp extends MongoBaseService {

    @Inject
    private KafkaProducer kafkaProducer;
    @Inject
    private SchedulerPanelRepository schedulerPanelRepository;

    public void pushToQueue() {

        //SchedulerPanel panel = schedulerPanelRepository.findOne(new BigInteger());

        KairosSchedulerExecutorDTO job = new KairosSchedulerExecutorDTO();
        job.setJobType(JobType.FUNCTIONAL);

       // ObjectMapperUtils.copyProperties(panel,job);
        //kafkaProducer.pushToQueue(job);


    }
}
