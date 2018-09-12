package com.kairos.service.scheduler_service;

import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.kafka.JobQueueExecutor;
import com.kairos.service.dashboard.SickService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerToActivityQueueService implements JobQueueExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueService.class);
    @Inject
    private SickService sickService;

    public void execute(KairosSchedulerExecutorDTO job) {

        switch (job.getJobSubType()) {
            case FLIP_PHASE:
                logger.info("JOB for flipping phase");
                break;
            case UPDATE_USER_ABSENCE:
                logger.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                sickService.checkStatusOfUserAndUpdateStatus(job.getUnitId());
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
