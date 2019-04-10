package com.kairos.service.scheduler;


import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.service.staff.PositionService;
import com.kairos.service.unit_position.EmploymentJobService;
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

    private static Logger logger = LoggerFactory.getLogger(SchedulerToUserQueueService.class);

    public void execute(KairosSchedulerExecutorDTO job) {

        switch(job.getJobSubType()) {
            case INTEGRATION:
                integrationJobService.runJob(job);
                break;
            case EMPLOYMENT_END:
                positionService.endEmploymentProcess(job.getId(),job.getUnitId(),job.getEntityId().longValue(),DateUtils.getLocalDatetimeFromLong(job.getOneTimeTriggerDateMillis()));
                break;
            case QUESTIONAIRE_NIGHTWORKER:
                logger.info("Questionaire nightworker----------------->"+job.getId());
                break;
            case SENIORITY_LEVEL:
                employmentJobService.updateSeniorityLevelOnJobTrigger(job.getId(),job.getUnitId());
                break;

            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
