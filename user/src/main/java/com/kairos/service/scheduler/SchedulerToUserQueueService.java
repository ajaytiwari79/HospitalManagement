package com.kairos.service.scheduler;


import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.service.employment.EmploymentJobService;
import com.kairos.service.staff.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerToUserQueueService implements JobQueueExecutor {

    @Inject
    private PositionService positionService;
    @Inject
    private IntegrationJobsExecutorService integrationJobService;
    @Inject
    private EmploymentJobService employmentJobService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerToUserQueueService.class);

    public void execute(KairosSchedulerExecutorDTO job) {
        LOGGER.info("Job type----------------->{}",job.getJobSubType());
        switch(job.getJobSubType()) {
            case INTEGRATION:
                LOGGER.info("Integration----------------->{}",job.getId());
                integrationJobService.runJob(job);
                break;
            case POSITION_END:
                LOGGER.info("End Position----------------->{}",job.getId());
                positionService.endPositionProcess();
                break;
            case QUESTIONAIRE_NIGHTWORKER:
                LOGGER.info("Questionaire nightworker----------------->{}",job.getId());
                break;
            case SENIORITY_LEVEL:
                LOGGER.info("Update Seniority Level----------------->{}",job.getId());
                employmentJobService.updateSeniorityLevelOnJobTrigger(job.getId(),job.getUnitId());
                break;
            case NIGHT_WORKER:
                employmentJobService.updateNightWorkers();
                break;
            default:
                LOGGER.error("No execution route found for jobsubtype");
                break;
        }

    }
}
