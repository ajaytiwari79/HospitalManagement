package com.kairos.service.scheduler_service;

import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.controller.unit_settings.ProtectedDaysOffController;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.service.dashboard.SickService;
import com.kairos.service.payroll_setting.UnitPayrollSettingService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.shift.ShiftReminderService;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import com.kairos.service.wta.WorkTimeAgreementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.DateUtils.getStartOfDay;

@Service
public class SchedulerToActivityQueueService implements JobQueueExecutor {

    @Inject
    private PlanningPeriodService planningPeriodService;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerToActivityQueueService.class);
    @Inject
    private SickService sickService;
    @Inject
    ShiftReminderService shiftReminderService;
    @Inject
    private TimeAndAttendanceService timeAndAttendanceService;
    @Inject
    private UnitPayrollSettingService unitPayrollSettingService;
    @Inject
    private ActivityService activityService;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Override
    public void execute(KairosSchedulerExecutorDTO job) {
        logger.info("Job type----------------->"+job.getJobSubType());
        switch (job.getJobSubType()) {
            case FLIP_PHASE:
                logger.info("JOB for flipping phase");
                planningPeriodService.updateFlippingDate(job.getEntityId(), job.getUnitId(), job.getId());
                break;
            case UPDATE_USER_ABSENCE:
                logger.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                sickService.checkStatusOfUserAndUpdateStatus(job.getUnitId());
                break;
            case SHIFT_REMINDER:
                logger.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                shiftReminderService.sendReminderViaEmail(job);
                break;
            case ATTENDANCE_SETTING:
                logger.info("Job to update clock out time");
                timeAndAttendanceService.checkOutBySchedulerJob(job.getUnitId(), getStartOfDay(getDate()),null);
                break;
            case ADD_PAYROLL_PERIOD:
                logger.info("Job to create MONTHLY and FORTNIGHTLY  payroll period ");
                unitPayrollSettingService.addPayrollPeriodInUnitViaJobOrManual(Arrays.asList(PayrollFrequency.MONTHLY, PayrollFrequency.FORTNIGHTLY), null);
                break;
            case ADD_PLANNING_PERIOD:
                logger.info("Job to add planning period ");
                planningPeriodService.addPlanningPeriodViaJob();
                break;
            case PROTECTED_DAYS_OFF:
                logger.info("Job to protected days off ");
                workTimeAgreementBalancesCalculationService.setProtectedDaysOffHoursViaJob();
                break;
            case UNASSIGN_EXPERTISE_FROM_ACTIVITY:
                logger.info("Job to Unassign expertise from activity ");
                activityService.unassighExpertiseFromActivities(job.getEntityId());
                break;
            default:
                logger.error("No exceution route found for jobsubtype");
                break;

        }

    }
}
