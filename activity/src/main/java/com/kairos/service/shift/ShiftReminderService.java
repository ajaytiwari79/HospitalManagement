package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.scheduler.scheduler_panel.SchedulerPanelDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.scheduler.JobSubType;
import com.kairos.enums.scheduler.JobType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.*;
import com.kairos.scheduler_listener.ActivityToSchedulerQueueService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.SHIFT_EMAIL_BODY;
import static com.kairos.constants.AppConstants.SHIFT_NOTIFICATION;

/**
 * CreatedBy vipulpandey on 15/10/18
 **/
@Service
public class ShiftReminderService extends MongoBaseService {
    @Inject
    ActivityMongoRepository activityMongoRepository;
    @Inject
    private MailService mailService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private SchedulerServiceRestClient schedulerServiceRestClient;
    @Inject private
    UserRestClientForScheduler userRestClientForScheduler;
    @Inject
    ActivityToSchedulerQueueService activityToSchedulerQueueService;

    private final static Logger logger = LoggerFactory.getLogger(ShiftReminderService.class);

    public void updateReminderTrigger(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        // TODO Find better approach
        /*List<BigInteger> jobIds = shift.getActivities().stream().map(ShiftActivity::getId).collect(Collectors.toList());
        deleteReminderTrigger(jobIds, shift.getUnitId());
        setReminderTrigger(activityWrapperMap, shift);*/
    }

    public void setReminderTrigger(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        /*List<SchedulerPanelDTO> scheduledJobs = new ArrayList<>(shift.getActivities().size());
        shift.getActivities().forEach(currentShift -> {
            if (!currentShift.isBreakShift() && activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().isAllowCommunicationReminder()
                    && !activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().getActivityReminderSettings().isEmpty()) {
                LocalDateTime firstReminderDateTime = calculateTriggerTime(activityWrapperMap.get(currentShift.getActivityId()).getActivity(), shift.getStartDate(), DateUtils.getCurrentLocalDateTime());
                if (firstReminderDateTime != null) {
                    scheduledJobs.add(new SchedulerPanelDTO(shift.getUnitId(), JobType.FUNCTIONAL, JobSubType.SHIFT_REMINDER, currentShift.getId(), firstReminderDateTime, true, null));
                } else {
                    logger.info("Unable to get notify time for shift {}", shift.getId());
                }
            }
        });
        if (!scheduledJobs.isEmpty()) {
            // TODO FUTURE REMOVE VIPUL MIGHT WE DONT NEED
            List<SchedulerPanelDTO> schedulerPanelRestDTOS = schedulerServiceRestClient.publishRequest
                    (scheduledJobs, shift.getUnitId(), true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
                    });
            //save(shift);
        }*/
    }

    public void deleteReminderTrigger(List<BigInteger> jobIds, Long unitId) {
        schedulerServiceRestClient.publishRequest(jobIds, unitId, true, IntegrationOperation.DELETE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, null, null);


    }

    private LocalDateTime calculateTriggerTime(Activity activity, Date shiftStartDate, LocalDateTime currentLocalDateTime) {
        LocalDateTime shiftStartDateTime = DateUtils.asLocalDateTime(shiftStartDate);
        long daysRemaining = currentLocalDateTime.until(shiftStartDateTime, ChronoUnit.DAYS);
        long minutesRemaining = currentLocalDateTime.until(shiftStartDateTime, ChronoUnit.MINUTES);
        LocalDateTime triggerDateTime = null;
        if (daysRemaining > 0) {
            triggerDateTime = calculateNextTrigger(activity, currentLocalDateTime, DurationType.DAYS, shiftStartDateTime, daysRemaining);
        } else if (minutesRemaining > 0) {
            triggerDateTime = calculateNextTrigger(activity, currentLocalDateTime, DurationType.MINUTES, shiftStartDateTime, minutesRemaining);
        }
        return triggerDateTime;
    }

    public void sendReminderViaEmail(KairosSchedulerExecutorDTO jobDetails) {
        Shift shift = shiftMongoRepository.findShiftByShiftActivityId(jobDetails.getEntityId());
        if (!Optional.ofNullable(shift).isPresent()) {
            logger.info("Unable to find shift by id {}", jobDetails.getEntityId());
        }
        Optional<ShiftActivity> shiftActivity = shift.getActivities().stream().filter(currentShiftActivity ->
                currentShiftActivity.getId().equals(jobDetails.getEntityId())).findAny();
        if (!shiftActivity.isPresent()) {
            logger.info("Unable to find current shift Activity by id {}", jobDetails.getEntityId());
        }
        Activity activity = activityMongoRepository.findOne(shiftActivity.get().getActivityId());

        StaffDTO staffDTO = genericIntegrationService.getStaff(shift.getUnitId(), shift.getStaffId());
        LocalDateTime lastTriggerDateTime = DateUtils.getLocalDateTimeFromMillis(jobDetails.getOneTimeTriggerDateMillis());
        LocalDateTime nextTriggerDateTime = calculateTriggerTime(activity, shiftActivity.get().getStartDate(), lastTriggerDateTime);

        String content = String.format(SHIFT_EMAIL_BODY, staffDTO.getFirstName(), shiftActivity.get().getActivityName(), DateUtils.asLocalDate(shiftActivity.get().getStartDate()),
                shiftActivity.get().getStartDate().getHours() + " : " + shiftActivity.get().getStartDate().getMinutes());
        mailService.sendPlainMail(staffDTO.getEmail(), content, SHIFT_NOTIFICATION);


        if (nextTriggerDateTime != null && nextTriggerDateTime.isBefore(DateUtils.asLocalDateTime(shiftActivity.get().getStartDate()))) {
            logger.info("next email on {} to staff {}", nextTriggerDateTime, staffDTO.getFirstName());
            List<SchedulerPanelDTO> schedulerPanelRestDTOS = genericIntegrationService.registerNextTrigger(shift.getUnitId(),Arrays.asList(new SchedulerPanelDTO(shift.getUnitId(), JobType.FUNCTIONAL, JobSubType.SHIFT_REMINDER, shiftActivity.get().getId(), nextTriggerDateTime, true, null)));

        }


    }

    LocalDateTime calculateNextTrigger(Activity activity, LocalDateTime lastTriggerDateTime, DurationType durationType, LocalDateTime shiftDateTime, long remainingUnit) {
        LocalDateTime nextTriggerDateTime = null;
        for (ActivityReminderSettings current : activity.getCommunicationActivityTab().getActivityReminderSettings()) {

            if (current.getSendReminder().getDurationType().equals(durationType)) {
                /**
                 * left 4 days
                 * send reminder every 2 hours
                 */
                if (current.getSendReminder().getTimeValue() <= remainingUnit && current.getRepeatReminder().getDurationType().compareTo(DurationType.DAYS) != 1) {
                    nextTriggerDateTime = DateUtils.substractDurationInLocalDateTime(shiftDateTime, current.getSendReminder().getTimeValue(), current.getSendReminder().getDurationType());
                    if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                        nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                    }
                    break;
                }
                if (current.getSendReminder().getTimeValue() >= remainingUnit && durationType != current.getRepeatReminder().getDurationType()) {
                    nextTriggerDateTime = lastTriggerDateTime;
                    if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                        nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                    }
                    break;
                }
                if (current.isRepeatAllowed() && remainingUnit >= current.getRepeatReminder().getTimeValue()) {
                    nextTriggerDateTime = DateUtils.addDurationInLocalDateTime(lastTriggerDateTime, current.getRepeatReminder().getTimeValue(), current.getRepeatReminder().getDurationType(), 1);
                    break;
                }
                /** if directly send reminder is greater than current value
                 *remaining days 3
                 * "sendReminder":2,
                 **/
                if (current.getSendReminder().getDurationType() == durationType && remainingUnit >= current.getSendReminder().getTimeValue()) {
                    nextTriggerDateTime = DateUtils.substractDurationInLocalDateTime(shiftDateTime, current.getSendReminder().getTimeValue(), current.getSendReminder().getDurationType());
                    break;
                }
            } else if (current.getRepeatReminder().getDurationType().equals(durationType)) {
                nextTriggerDateTime = lastTriggerDateTime;
                if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                    nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                }
                break;
            } else {
                nextTriggerDateTime = lastTriggerDateTime;
                if (durationType==DurationType.DAYS && current.getSendReminder().getDurationType() == DurationType.MINUTES){

                    nextTriggerDateTime = nextTriggerDateTime.plusMinutes(remainingUnit*24*60);
                }
                else if (current.getSendReminder().getDurationType() == DurationType.MINUTES) {
                    nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                }
            }
        }
        return nextTriggerDateTime;
    }
}
