package com.kairos.service.kafka;

import java.util.List;
import com.kairos.dto.KairosSchedulerExecutorDTO;
import com.kairos.service.scheduler.IntegrationJobsExecutorService;
import com.kairos.service.staff.EmploymentService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;

@Service
public class SchedulerToUserQueueService implements JobQueueExecutor {

    @Inject
    private EmploymentService employmentService;
    @Inject
    private IntegrationJobsExecutorService integrationJobService;
    
    public void execute(KairosSchedulerExecutorDTO job) {

        switch(job.getJobSubType()) {
            case INTEGRATION:
                integrationJobService.runJob(job);
                break;
            case EMPLOYMENT_END:
                List<Long> employmentIds = new ArrayList<Long>();
                employmentIds.add(job.getEntityId().longValue());
                employmentService.moveToReadOnlyAccessGroup(employmentIds);
                break;


        }

    }
}
