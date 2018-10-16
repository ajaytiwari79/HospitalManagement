package com.kairos.service.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.config.env.EnvConfig;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.scheduler.queue.KairosScheduleJobDTO;
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
import com.kairos.rest_client.RestTemplateResponseEnvelope;
import com.kairos.rest_client.SchedulerServiceRestClient;
import com.kairos.rest_client.StaffRestClient;
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
    private StaffRestClient staffRestClient;
    @Inject
    private SchedulerServiceRestClient schedulerServiceRestClient;
    @Inject
    EnvConfig envConfig;

    private final static Logger logger = LoggerFactory.getLogger(ShiftReminderService.class);

    public void updateReminderTrigger(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        // TODO Find better approach
        deleteReminderTrigger(shift.getId(), shift.getUnitId());
        setReminderTrigger(activityWrapperMap, shift);
    }

    public void setReminderTrigger(Map<BigInteger, ActivityWrapper> activityWrapperMap, Shift shift) {
        List<SchedulerPanelDTO> scheduledJobs = new ArrayList<>(shift.getActivities().size());
        shift.getActivities().forEach(currentShift -> {
            if (activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().isAllowCommunicationReminder()) {
                if (!activityWrapperMap.get(currentShift.getActivityId()).getActivity().getCommunicationActivityTab().getActivityReminderSettings().isEmpty()) {
                    LocalDateTime firstReminderDateTime = calculateTriggerTime(activityWrapperMap.get(currentShift.getActivityId()).getActivity(), shift.getStartDate(), DateUtils.getCurrentLocalDateTime());
                    if (firstReminderDateTime != null) {
                        scheduledJobs.add(new SchedulerPanelDTO(shift.getUnitId(), JobType.FUNCTIONAL, JobSubType.SHIFT_REMINDER, shift.getId(), firstReminderDateTime, true, currentShift.getActivityId() + ""));
                    } else {
                        logger.info("Unable to get notify time for shift {}", shift.getId());
                    }
                }
            }
        });
        // MIGHT WE DONT NEED
        List<SchedulerPanelDTO> schedulerPanelRestDTOS = schedulerServiceRestClient.publishRequest(scheduledJobs, shift.getUnitId(), true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
        });

        if (!schedulerPanelRestDTOS.isEmpty()) {
            schedulerPanelRestDTOS.forEach(schedulerPanelDTO -> {
                shift.getActivities().forEach(currentShift -> {
                    if (schedulerPanelDTO.getFilterId() == (currentShift.getActivityId().toString())) {
                        currentShift.setJobId(schedulerPanelDTO.getId());
                    }
                });
            });
        }
        save(shift);
    }

    public void deleteReminderTrigger(BigInteger shiftId, Long unitId) {
        schedulerServiceRestClient.publishRequest(Collections.singleton(shiftId), unitId, true, IntegrationOperation.DELETE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Boolean>>() {
        }, null);


    }

    private LocalDateTime calculateTriggerTime(Activity activity, Date shiftStartDate, LocalDateTime currentLocalDateTime) {
        LocalDateTime shiftStartDateTime = DateUtils.asLocalDateTime(shiftStartDate);
        long daysRemaining = currentLocalDateTime.until(shiftStartDateTime, ChronoUnit.DAYS);
        long hoursRemaining = currentLocalDateTime.until(shiftStartDateTime, ChronoUnit.HOURS);
        long minutesRemaining = currentLocalDateTime.until(shiftStartDateTime, ChronoUnit.MINUTES);
        LocalDateTime firstTriggerDateTime = null;
        if (daysRemaining > 0) {
            firstTriggerDateTime = registerNextTrigger(activity, currentLocalDateTime, DurationType.DAYS, shiftStartDateTime, daysRemaining);
        } else if (hoursRemaining > 0) {
            firstTriggerDateTime = registerNextTrigger(activity, currentLocalDateTime, DurationType.HOURS, shiftStartDateTime, hoursRemaining);
        } else if (minutesRemaining > 0) {
            firstTriggerDateTime = registerNextTrigger(activity, currentLocalDateTime, DurationType.MINUTES, shiftStartDateTime, minutesRemaining);
        }
        return firstTriggerDateTime;
    }

    public void sendReminderForEmail(KairosSchedulerExecutorDTO jobDetails) {
        Optional<Shift> shift = shiftMongoRepository.findById(jobDetails.getEntityId());
        if (!shift.isPresent()) {
            logger.info("Unable to find shift by id {}", jobDetails.getEntityId());
        }
        Activity activity = activityMongoRepository.findOne(new BigInteger(jobDetails.getFilterId()));
        StaffDTO staffDTO = staffRestClient.getStaff(shift.get().getStaffId());

        LocalDateTime lastTriggerDateTime = DateUtils.getLocalDateTimeFromMillis(jobDetails.getOneTimeTriggerDateMillis());
        LocalDateTime nextTriggerDateTime = calculateTriggerTime(activity, shift.get().getStartDate(), lastTriggerDateTime);

        if (nextTriggerDateTime != null) {

            List<SchedulerPanelDTO> schedulerPanelRestDTOS = schedulerServiceRestClient.publishRequest(
                    Arrays.asList(new SchedulerPanelDTO(shift.get().getUnitId(), JobType.FUNCTIONAL, JobSubType.SHIFT_REMINDER, shift.get().getId(), nextTriggerDateTime, true, activity.getId() + "")), shift.get().getUnitId(), true, IntegrationOperation.CREATE, "/scheduler_panel", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<List<SchedulerPanelDTO>>>() {
                    });
            logger.info(DateUtils.getMillisFromLocalDateTime(schedulerPanelRestDTOS.get(0).getOneTimeTriggerDate()) + "");
        }
        String content = String.format(SHIFT_EMAIL_BODY, staffDTO.getFirstName(), DateUtils.asLocalDate(shift.get().getStartDate()),
                shift.get().getStartDate().getHours() + ":" + shift.get().getStartDate().getMinutes());
        if (envConfig.getCurrentProfile().equals("local")) {
            content = content + " next time " + nextTriggerDateTime;
        }
        mailService.sendPlainMail(staffDTO.getEmail(), content, SHIFT_NOTIFICATION);

    }

    LocalDateTime registerNextTrigger(Activity activity, LocalDateTime lastTriggerDateTime, DurationType durationType, LocalDateTime shiftDateTime, long remainingUnit) {
        LocalDateTime nextTriggerDateTime = null;
        for (ActivityReminderSettings current : activity.getCommunicationActivityTab().getActivityReminderSettings()) {

            if (current.getSendReminder().getDurationType().equals(durationType)) {
                /**
                 * left 4 days
                 * send reminder every 2 hours
                 */
                if (current.getSendReminder().getTimeValue() <= remainingUnit && current.getRepeatReminder().getDurationType().compareTo(DurationType.DAYS) != 1) {
                    nextTriggerDateTime = DateUtils.substractDurationInLocalDateTime(shiftDateTime, current.getSendReminder().getTimeValue(), current.getSendReminder().getDurationType());
                    if (current.getRepeatReminder().getDurationType() == DurationType.HOURS) {
                        nextTriggerDateTime = nextTriggerDateTime.plusHours(current.getRepeatReminder().getTimeValue());
                    } else if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                        nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                    }
                    break;
                }
                if (current.getSendReminder().getTimeValue() >= remainingUnit && durationType != current.getRepeatReminder().getDurationType()) {
                    nextTriggerDateTime = lastTriggerDateTime;
                    if (current.getRepeatReminder().getDurationType() == DurationType.HOURS) {
                        nextTriggerDateTime = nextTriggerDateTime.plusHours(current.getRepeatReminder().getTimeValue());
                    } else if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
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
                } else {
                    logger.info("unhandled case");
                }
            } else if (current.getRepeatReminder().getDurationType().equals(durationType)) {
                nextTriggerDateTime = lastTriggerDateTime;
                if (current.getRepeatReminder().getDurationType() == DurationType.HOURS) {
                    nextTriggerDateTime = nextTriggerDateTime.plusHours(current.getRepeatReminder().getTimeValue());
                } else if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                    nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                }
                break;

            }
        }
        logger.info("next email on {} ", nextTriggerDateTime);
        return nextTriggerDateTime;
    }
}
