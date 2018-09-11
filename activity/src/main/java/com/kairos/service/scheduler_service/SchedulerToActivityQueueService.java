package com.kairos.service.scheduler_service;

import com.kairos.commons.utils.DateUtils;
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
            case USER_SICK:
                logger.info("JOB for update health of sick user");
                sickService.checkStatusOfUserAndUpdateStatus(job.getUnitId());
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
