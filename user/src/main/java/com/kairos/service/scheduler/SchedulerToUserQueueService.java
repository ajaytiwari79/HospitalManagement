package com.kairos.service.scheduler;


import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.scheduler.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.kafka.JobQueueExecutor;
import com.kairos.service.scheduler.IntegrationJobsExecutorService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.service.unit_position.UnitPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerToUserQueueService implements JobQueueExecutor {

    @Inject
    private EmploymentService employmentService;
    @Inject
    private IntegrationJobsExecutorService integrationJobService;
    @Inject
    private UnitPositionService unitPositionService;

    private static Logger logger = LoggerFactory.getLogger(SchedulerToUserQueueService.class);

    public void execute(KairosSchedulerExecutorDTO job) {

        switch(job.getJobSubType()) {
            case INTEGRATION:
                integrationJobService.runJob(job);
                break;
            case EMPLOYMENT_END:
                employmentService.endEmploymentProcess(job.getId(),job.getUnitId(),job.getEntityId().longValue(),DateUtils.getLocalDatetimeFromLong(job.getOneTimeTriggerDateMillis()));
                break;
            case QUESTIONAIRE_NIGHTWORKER:
                logger.info("Questionaire nightworker----------------->"+job.getId());
                break;
            case SENIORITY_LEVEL:
                unitPositionService.updateSeniorityLevelOnJobTrigger();
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
