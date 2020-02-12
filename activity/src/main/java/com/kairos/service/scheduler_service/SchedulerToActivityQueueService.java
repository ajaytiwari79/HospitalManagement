package com.kairos.service.scheduler_service;

import com.kairos.commons.service.scheduler.queue.JobQueueExecutor;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.enums.payroll_setting.PayrollFrequency;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.attendence_setting.TimeAndAttendanceService;
import com.kairos.service.dashboard.SickService;
import com.kairos.service.payroll_setting.UnitPayrollSettingService;
import com.kairos.service.period.PlanningPeriodService;
import com.kairos.service.shift.ActivityReminderService;
import com.kairos.service.shift.ShiftReminderService;
import com.kairos.service.time_bank.TimeBankCalculationService;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerToActivityQueueService.class);
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
    private TimeBankCalculationService timeBankCalculationService;
    @Inject
    private WorkTimeAgreementBalancesCalculationService workTimeAgreementBalancesCalculationService;
    @Inject
    private ActivityReminderService activityReminderService;

    @Override
    public void execute(KairosSchedulerExecutorDTO job) {
        LOGGER.info("Job type----------------->{}",job.getJobSubType());
        switch (job.getJobSubType()) {
            case FLIP_PHASE:
                LOGGER.info("JOB for flipping phase");
                planningPeriodService.updateFlippingDate(job.getEntityId(), job.getUnitId(), job.getId());
                break;
            case UPDATE_USER_ABSENCE:
                LOGGER.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                sickService.checkStatusOfUserAndUpdateStatus(job.getUnitId());
                break;
            case SHIFT_REMINDER:
                LOGGER.info("Job to update sick absence user and if user is not sick then add more sick shifts");
                shiftReminderService.sendReminderViaEmail(job);
                break;
            case ATTENDANCE_SETTING:
                LOGGER.info("Job to update clock out time");
                timeAndAttendanceService.checkOutBySchedulerJob(job.getUnitId(), getStartOfDay(getDate()),null);
                break;
            case ADD_PAYROLL_PERIOD:
                LOGGER.info("Job to create MONTHLY and FORTNIGHTLY  payroll period ");
                unitPayrollSettingService.addPayrollPeriodInUnitViaJobOrManual(Arrays.asList(PayrollFrequency.MONTHLY, PayrollFrequency.FORTNIGHTLY), null);
                break;
            case ADD_PLANNING_PERIOD:
                LOGGER.info("Job to add planning period ");
                planningPeriodService.addPlanningPeriodViaJob();
                break;
            case PROTECTED_DAYS_OFF:
                LOGGER.info("Job to protected days off ");
                timeBankCalculationService.new CalculatePlannedHoursAndScheduledHours().updateTimeBankAgainstProtectedDaysOffSetting();
                break;
            case WTA_LEAVE_COUNT:
                LOGGER.info("Job to protected days off ");
                workTimeAgreementBalancesCalculationService.updateWTALeaveCountByJob(job.getEntityId().longValue());
                break;
            case UNASSIGN_EXPERTISE_FROM_ACTIVITY:
                LOGGER.info("Job to Unassign expertise from activity ");
                activityService.unassighExpertiseFromActivities(job.getEntityId());
                break;
            case ACTIVITY_CUTOFF:
                LOGGER.info("Job to Reminders to be sent to the staff for not planning the absences within the cutoff period. ");
                activityReminderService.sendReminderViaEmail(job);
                break;
            default:
                LOGGER.error("No exceution route found for jobsubtype");
                break;

        }

    }
}
