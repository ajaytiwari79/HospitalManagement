package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.dto.activity.shift.ActivityShiftStatusSettingsDTO;
import com.kairos.dto.scheduler.queue.KairosSchedulerExecutorDTO;
import com.kairos.dto.user.staff.StaffDTO;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ActivityShiftStatusSettingsRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.rest_client.StaffRestClient;
import com.kairos.scheduler_listener.ActivityToSchedulerQueueService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kairos.constants.AppConstants.SHIFT_EMAIL_BODY;
import static com.kairos.constants.AppConstants.SHIFT_NOTIFICATION;

@Service
@Transactional
public class ActivityShiftStatusSettingsService extends MongoBaseService {

    @Inject
    private ActivityShiftStatusSettingsRepository activityAndShiftStatusSettingsRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    ActivityMongoRepository activityMongoRepository;
    @Inject
    ActivityToSchedulerQueueService activityToSchedulerQueueService;
    @Inject
    private MailService mailService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private StaffRestClient staffRestClient;
    private final static Logger logger = LoggerFactory.getLogger(ActivityShiftStatusSettingsService.class);


    public ActivityShiftStatusSettingsDTO addActivityAndShiftStatusSetting(Long unitId, ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO) {
        ActivityShiftStatusSettings activityShiftStatusSettings = ObjectMapperUtils.copyPropertiesByMapper(activityShiftStatusSettingsDTO, ActivityShiftStatusSettings.class);
        activityShiftStatusSettings.setUnitId(unitId);
        save(activityShiftStatusSettings);
        activityShiftStatusSettingsDTO.setId(activityShiftStatusSettings.getId());
        return activityShiftStatusSettingsDTO;
    }


    public ActivityShiftStatusSettingsDTO updateActivityAndShiftStatusSettings(Long unitId, BigInteger id, ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO) {
        Optional<ActivityShiftStatusSettings> activityAndShiftStatusSettings = activityAndShiftStatusSettingsRepository.findById(id);
        if (!activityAndShiftStatusSettings.isPresent()) {
            exceptionService.dataNotFoundException("settings.not.found", id);
        }
        ObjectMapperUtils.copyProperties(activityShiftStatusSettingsDTO, activityAndShiftStatusSettings.get());
        activityAndShiftStatusSettings.get().setUnitId(unitId);
        save(activityAndShiftStatusSettings.get());
        return activityShiftStatusSettingsDTO;

    }

    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long unitId, BigInteger activityId) {
        return activityAndShiftStatusSettingsRepository.getActivityAndShiftStatusSettingsGroupedByStatus(unitId, activityId);
    }
    Map<Object ,Object >  map= new LinkedHashMap<>();
    public Map<Object ,Object >  setReminderTrigger(BigInteger activityId) {
        Activity activity = activityMongoRepository.findOne(activityId);
        logger.info("reminder is enabled {}", activity.getCommunicationActivityTab().isAllowCommunicationReminder());

        Optional<Shift> shift = shiftMongoRepository.findById(new BigInteger("316"));
        if (!shift.isPresent()) {
            logger.info("Unable to find shift by id {}", "316");
        }


        if (activity.getCommunicationActivityTab().isAllowCommunicationReminder()) {
            if (!activity.getCommunicationActivityTab().getActivityReminderSettings().isEmpty()) {
                ActivityReminderSettings firstSettings = activity.getCommunicationActivityTab().getActivityReminderSettings().get(0);
                // TODO This is shift startDate


                LocalDateTime firstReminderDateTime = DateUtils.substractDurationInLocalDateTime(LocalDateTime.now().withYear(2019).withMonth(01).withDayOfMonth(01).withHour(10).withMinute(00), firstSettings.getSendReminder().getTimeValue(), firstSettings.getSendReminder().getDurationType());
                //activityToSchedulerQueueService.pushToJobQueueForShiftReminder(999L, new BigInteger("5"),  firstReminderDateTime);
                map.put(firstReminderDateTime,null);
                KairosSchedulerExecutorDTO jobDetails = new KairosSchedulerExecutorDTO();
                jobDetails.setEntityId(new BigInteger("316"));
                jobDetails.setOneTimeTriggerDateMillis(DateUtils.getMillisFromLocalDateTime(firstReminderDateTime));
                sendReminderForEmail(jobDetails);
            }
        }
        return map;
    }

    public void sendReminderForEmail(KairosSchedulerExecutorDTO jobDetails) {
        Optional<Shift> shift = shiftMongoRepository.findById(jobDetails.getEntityId());
        if (!shift.isPresent()) {
            logger.info("Unable to find shift by id {}", jobDetails.getEntityId());
        }
        Activity activity = activityMongoRepository.findOne(shift.get().getActivities().get(0).getActivityId());
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setEmail("vipulp1293@gmail.com");
        staffDTO.setFirstName("vipulp1293@gmail.com");
        //staffRestClient.getStaff(shift.get().getStaffId());
        LocalDateTime shiftStartDate = DateUtils.asLocalDateTime(shift.get().getStartDate());
        LocalDateTime lastTriggerDateTime = DateUtils.getLocalDateTimeFromMillis(jobDetails.getOneTimeTriggerDateMillis());

        long daysRemaining = lastTriggerDateTime.until(shiftStartDate, ChronoUnit.DAYS);
        long hoursRemaining = lastTriggerDateTime.until(shiftStartDate, ChronoUnit.HOURS);
        long minutesRemaining = lastTriggerDateTime.until(shiftStartDate, ChronoUnit.MINUTES);

        if (daysRemaining > 0) {
            registerNextTrigger(activity, lastTriggerDateTime, DurationType.DAYS, shiftStartDate, daysRemaining);
        } else if (hoursRemaining > 0) {
            registerNextTrigger(activity, lastTriggerDateTime, DurationType.HOURS, shiftStartDate, hoursRemaining);
        } else if (minutesRemaining > 0) {
            registerNextTrigger(activity, lastTriggerDateTime, DurationType.MINUTES, shiftStartDate, minutesRemaining);
        }
        //    mailService.sendPlainMail(staffDTO.getEmail(), String.format(SHIFT_EMAIL_BODY, staffDTO.getFirstName(), DateUtils.asLocalDate(shift.get().getStartDate()),
        //          shift.get().getStartDate().getHours() + ":" + shift.get().getStartDate().getMinutes())
        //         , SHIFT_NOTIFICATION);

    }

    void registerNextTrigger(Activity activity, LocalDateTime lastTriggerDateTime, DurationType durationType, LocalDateTime shiftDateTime, long remainingUnit) {
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
                if ( current.getSendReminder().getTimeValue() >= remainingUnit && durationType!=current.getRepeatReminder().getDurationType()) {
                   nextTriggerDateTime=lastTriggerDateTime;
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
                if (current.getSendReminder().getDurationType()==durationType && remainingUnit >= current.getSendReminder().getTimeValue()) {
                    nextTriggerDateTime = DateUtils.substractDurationInLocalDateTime(shiftDateTime, current.getSendReminder().getTimeValue(), current.getSendReminder().getDurationType());
                    break;
                }
                else {
                    logger.info("unhandled case");
                }
            }
            else if (current.getRepeatReminder().getDurationType().equals(durationType)) {
                nextTriggerDateTime=lastTriggerDateTime;
                if (current.getRepeatReminder().getDurationType() == DurationType.HOURS) {
                    nextTriggerDateTime = nextTriggerDateTime.plusHours(current.getRepeatReminder().getTimeValue());
                } else if (current.getRepeatReminder().getDurationType() == DurationType.MINUTES) {
                    nextTriggerDateTime = nextTriggerDateTime.plusMinutes(current.getRepeatReminder().getTimeValue());
                }
                break;

            }
        }
        logger.info("I have to send email on {}", nextTriggerDateTime);
        if (nextTriggerDateTime != null) {
            map.put(nextTriggerDateTime,null);
            KairosSchedulerExecutorDTO jobDetails = new KairosSchedulerExecutorDTO();
            jobDetails.setEntityId(new BigInteger("316"));
            jobDetails.setOneTimeTriggerDateMillis(DateUtils.getMillisFromLocalDateTime(nextTriggerDateTime));
            sendReminderForEmail(jobDetails);

            //activityToSchedulerQueueService.pushToJobQueueForShiftReminder(999L, new BigInteger("5"), nextTriggerDateTime);
        }
    }
}