package com.kairos.service.scheduler_service;

import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.queue.kafka.JobQueueExecutor;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.dashboard.SickService;
import com.kairos.service.shift.ShiftReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class SchedulerToActivityQueueService implements JobQueueExecutor {

   @Inject
    private PlanningPeriodService planningPeriodService;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueService.class);
    @Inject
    private SickService sickService;
    @Inject
    ShiftReminderService shiftReminderService;

    @Override
    public void execute(KairosSchedulerExecutorDTO job) {

        switch (job.getJobSubType()) {
            case FLIP_PHASE:
                planningPeriodService.updateFlippingDate(job.getEntityId(),job.getUnitId(),job.getId());
                logger.info("JOB for flipping phase");
                break;
            case UPDATE_USER_ABSENCE:
                logger.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                sickService.checkStatusOfUserAndUpdateStatus(job.getUnitId());
                break;
            case SHIFT_REMINDER:
                logger.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                shiftReminderService.sendReminderForEmail(job);
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;
        }

    }
}
