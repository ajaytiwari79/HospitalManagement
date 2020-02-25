package com.kairos.service.scheduler_service;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.CutOffInterval;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobFrequencyType;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.period.PeriodPhaseFlippingDate;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.service.MongoBaseService;
import com.kairos.service.shift.ShiftReminderService;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.service.period.PlanningPeriodService.SCHEDULER_PANEL;

@Service
public class ActivitySchedulerJobService extends MongoBaseService {

    @Inject private SchedulerServiceRestClient schedulerRestClient;
    @Inject private ShiftReminderService shiftReminderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivitySchedulerJobService.class);

    public void registerJobForWTALeaveCount(Long countryId) {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(BigInteger.valueOf(countryId),newArrayList(DayOfWeek.values()), LocalTime.of(0, 10), JobType.SYSTEM, JobSubType.WTA_LEAVE_COUNT, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

    public void registerJobForProtectedDaysOff() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 5), JobType.SYSTEM, JobSubType.PROTECTED_DAYS_OFF, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
    }

    public void registerJobForNightWorker() {
        SchedulerPanelDTO schedulerPanelDTO = new SchedulerPanelDTO(newArrayList(DayOfWeek.values()), LocalTime.of(0, 1), JobType.SYSTEM, JobSubType.NIGHT_WORKER, ZoneId.systemDefault().toString());
        schedulerRestClient.publishRequest(newArrayList(schedulerPanelDTO), null, false, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
    }

    public boolean createJobForAddPayrollPeriod() {
        List<SchedulerPanelDTO> schedulerPanelDTOS = Arrays.asList(new SchedulerPanelDTO(JobType.SYSTEM, JobSubType.ADD_PAYROLL_PERIOD, JobFrequencyType.MONTHLY, getLocalDateTime(getFirstDayOfMonth(getLocalDate()), 01, 00, 00), false));
        LOGGER.info("create job for add payroll period");
        schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, null, true, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
        LOGGER.info("job registered of add payroll period");
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }

    //get current date for test use after test getLocalDateTime(getFirstDayOfMonth(getLocalDate()), 02, 00, 00)
    public boolean createJobOfPlanningPeriod() {
        List<SchedulerPanelDTO> schedulerPanelDTOS = Arrays.asList(new SchedulerPanelDTO(JobType.SYSTEM, JobSubType.ADD_PLANNING_PERIOD, JobFrequencyType.MONTHLY, getLocalDateTime((getLocalDate()), 07, 00, 00), false));
        LOGGER.info("create job for add planning period");
        schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, null, true, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });
        LOGGER.info("job registered of add planning period");
        return isCollectionNotEmpty(schedulerPanelDTOS);
    }

    public List<SchedulerPanelDTO> createScheduleJobForFlipPhase(List<PlanningPeriod> planningPeriods, Map<Long, String> unitAndTimeZoneMap) {
        List<SchedulerPanelDTO> schedulerPanelDTOS = new ArrayList<>();
        for (PlanningPeriod planningPeriod : planningPeriods) {
            for (PeriodPhaseFlippingDate periodPhaseFlippingDate : planningPeriod.getPhaseFlippingDate()) {
                if (periodPhaseFlippingDate.getFlippingDate() != null && periodPhaseFlippingDate.getFlippingTime() != null) {
                    schedulerPanelDTOS.add(new SchedulerPanelDTO(planningPeriod.getUnitId(), JobType.FUNCTIONAL, JobSubType.FLIP_PHASE, true, LocalDateTime.of(periodPhaseFlippingDate.getFlippingDate(), periodPhaseFlippingDate.getFlippingTime()), planningPeriod.getId(), unitAndTimeZoneMap.get(planningPeriod.getUnitId())));
                }
            }
        }
        if(isCollectionNotEmpty(schedulerPanelDTOS)){
            schedulerPanelDTOS = schedulerRestClient.publishRequest(schedulerPanelDTOS, -1l, true, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
            });
        }
        return schedulerPanelDTOS;
    }


    public void updateJobForShiftReminder(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        List<SchedulerPanelDTO> scheduledJobs = new ArrayList<>(shift.getActivities().size());
        shift.getActivities().forEach(currentShift -> {
            if (activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().isAllowCommunicationReminder()
                    && !activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().getActivityReminderSettings().isEmpty()) {
                LocalDateTime firstReminderDateTime = shiftReminderService.calculateTriggerTime(activityWrapperMap.get(currentShift.getActivityId()).getActivity(), shift.getStartDate(), DateUtils.getCurrentLocalDateTime());
                if (firstReminderDateTime != null) {
                    scheduledJobs.add(new SchedulerPanelDTO(shift.getUnitId(), JobType.FUNCTIONAL, JobSubType.SHIFT_REMINDER, currentShift.getId(), firstReminderDateTime, true, currentShift.getActivityId().toString()));
                } else {
                    LOGGER.info("Unable to get notify time for shift {}", shift.getId());
                }
            }
        });
        if (!scheduledJobs.isEmpty()) {
            // TODO FUTURE REMOVE VIPUL MIGHT WE DONT NEED
            schedulerRestClient.publishRequest(scheduledJobs, shift.getUnitId(), true, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
});
        }
    }

    @Async
    public void registerJobForActivityCutoff(List<Activity> activities) {
        for (Activity activity : activities) {
            BasicNameValuePair jobSubType = new BasicNameValuePair("jobSubType", JobSubType.ACTIVITY_REMINDER.toString());
            schedulerRestClient.publishRequest(null, activity.getUnitId(), true, IntegrationOperation.DELETE, "/scheduler_panel/entity/{entityId}/delete_job", newArrayList(jobSubType), new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {}, activity.getId());
            List<SchedulerPanelDTO> schedulerPanelDTOS = new ArrayList<>();
            if(activity.getCommunicationActivityTab().isAllowActivityCutoffReminder()) {
                calculateTriggerDateTimeList(activity).forEach(reminderDateTime ->
                        schedulerPanelDTOS.add(new SchedulerPanelDTO(activity.getUnitId(), JobType.SYSTEM, JobSubType.ACTIVITY_REMINDER, activity.getId(), reminderDateTime, true, null))
                );
                if (isCollectionNotEmpty(schedulerPanelDTOS)) {
                    schedulerRestClient.publishRequest(schedulerPanelDTOS, activity.getUnitId(), true, IntegrationOperation.CREATE, SCHEDULER_PANEL, null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {});
                }
            }
        }
    }

    private Set<LocalDateTime> calculateTriggerDateTimeList(Activity activity) {
        Set<LocalDateTime> reminderDateTimes = new HashSet<>();
        for (ActivityReminderSettings activityCutoffReminderSetting : activity.getCommunicationActivityTab().getActivityCutoffReminderSettings()) {
            for (CutOffInterval cutOffInterval : activity.getRulesActivityTab().getCutOffIntervals()) {
                LocalDateTime reminderDateTime = substractDurationInLocalDateTime(asLocalDateTime(asDate(cutOffInterval.getEndDate())), activityCutoffReminderSetting.getSendReminder().getTimeValue(), activityCutoffReminderSetting.getSendReminder().getDurationType());
                reminderDateTimes.add(reminderDateTime);
                if (activityCutoffReminderSetting.isRepeatAllowed()) {
                    reminderDateTimes.addAll(calculateRepeatTriggerDateTimeList(activityCutoffReminderSetting, cutOffInterval, reminderDateTime));
                }
            }
        }
        return reminderDateTimes;
    }

    private Set<LocalDateTime> calculateRepeatTriggerDateTimeList(ActivityReminderSettings activityCutoffReminderSetting, CutOffInterval cutOffInterval, LocalDateTime reminderDateTime) {
        Set<LocalDateTime> repeatTriggerDateTimes = new HashSet<>();
        reminderDateTime = addDurationInLocalDateTime(reminderDateTime, activityCutoffReminderSetting.getRepeatReminder().getTimeValue(), activityCutoffReminderSetting.getRepeatReminder().getDurationType(), 1);
        while (reminderDateTime.isBefore(cutOffInterval.getEndDate().plusDays(1).atStartOfDay())){
            repeatTriggerDateTimes.add(reminderDateTime);
            reminderDateTime = addDurationInLocalDateTime(reminderDateTime, activityCutoffReminderSetting.getRepeatReminder().getTimeValue(), activityCutoffReminderSetting.getRepeatReminder().getDurationType(), 1);
        }
        return repeatTriggerDateTimes;
    }
}


