package com.kairos.service.scheduler_service;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.kafka.JobQueueExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class SchedulerToActivityQueueService implements JobQueueExecutor {

    private static Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueService.class);

    public void execute(KairosSchedulerExecutorDTO job) {

        switch(job.getJobSubType()) {
            case FLIP_PHASE:
                logger.info("JOB for flipping phase");
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
