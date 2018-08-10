package com.kairos.service.kafka;

import java.util.List;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.service.scheduler.IntegrationJobsExecutorService;
import com.kairos.service.staff.EmploymentService;
import com.kairos.util.DateUtil;
import com.kairos.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;

@Service
public class SchedulerToUserQueueService implements JobQueueExecutor {

    @Inject
    private EmploymentService employmentService;
    @Inject
    private IntegrationJobsExecutorService integrationJobService;

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
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
