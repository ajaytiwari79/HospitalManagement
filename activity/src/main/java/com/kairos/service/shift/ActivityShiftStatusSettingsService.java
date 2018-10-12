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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

    public void setReminderTrigger(BigInteger activityId) {
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
                LocalDateTime firstDate=DateUtils.getDate
                shift.get().getStartDate();
                LocalDateTime firstReminderDateTime = DateUtils.substractDurationInLocalDateTime(, firstSettings.getSendReminder().getTimeValue(), firstSettings.getSendReminder().getDurationType(), 1);
                //activityToSchedulerQueueService.pushToJobQueueForShiftReminder(999L, new BigInteger("5"),  firstReminderDateTime);
                KairosSchedulerExecutorDTO jobDetails = new KairosSchedulerExecutorDTO();
                jobDetails.setEntityId(new BigInteger("316"));
                jobDetails.setOneTimeTriggerDateMillis(DateUtils.getMillisFromLocalDateTime(firstReminderDateTime));
                sendReminderForEmail(jobDetails);
            }
        }
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
        LocalDateTime currentTriggerDateTime = DateUtils.getLocalDateTimeFromMillis(jobDetails.getOneTimeTriggerDateMillis());

        long daysRemaining = currentTriggerDateTime.until(shiftStartDate, ChronoUnit.DAYS);
        long hoursRemaining = currentTriggerDateTime.until(shiftStartDate, ChronoUnit.HOURS);
        long minutesRemaining = currentTriggerDateTime.until(shiftStartDate, ChronoUnit.MINUTES);

        if (daysRemaining > 0) {
            registerNextTrigger(activity, currentTriggerDateTime, DurationType.DAYS, shiftStartDate, daysRemaining);
        } else if (hoursRemaining > 0) {
            registerNextTrigger(activity, currentTriggerDateTime, DurationType.HOURS, shiftStartDate, hoursRemaining);
        } else if (minutesRemaining > 0) {
            registerNextTrigger(activity, currentTriggerDateTime, DurationType.MINUTES, shiftStartDate, minutesRemaining);
        }
    //    mailService.sendPlainMail(staffDTO.getEmail(), String.format(SHIFT_EMAIL_BODY, staffDTO.getFirstName(), DateUtils.asLocalDate(shift.get().getStartDate()),
      //          shift.get().getStartDate().getHours() + ":" + shift.get().getStartDate().getMinutes())
       //         , SHIFT_NOTIFICATION);

    }

    void registerNextTrigger(Activity activity, LocalDateTime currentTriggerDateTime, DurationType durationType, LocalDateTime shiftDateTime, long remainingUnit) {
        LocalDateTime nextTriggerDateTime = null;
        for (ActivityReminderSettings current : activity.getCommunicationActivityTab().getActivityReminderSettings()) {

            if (current.getSendReminder().getDurationType().equals(durationType)) {

                if (current.getSendReminder().getTimeValue() <= remainingUnit) {
                    nextTriggerDateTime = DateUtils.substractDurationInLocalDateTime(shiftDateTime, current.getRepeatReminder().getTimeValue(), current.getRepeatReminder().getDurationType(), 1);
                    break;
                }
                if (current.isRepeatAllowed() && remainingUnit >= current.getRepeatReminder().getTimeValue()) {
                    nextTriggerDateTime = DateUtils.addDurationInLocalDateTime(currentTriggerDateTime, current.getRepeatReminder().getTimeValue(), current.getRepeatReminder().getDurationType(), 1);
                    break;
                } else {
                    logger.info("unhandled case");
                }
            }
            }
        logger.info("I have to send email on {}", nextTriggerDateTime);
        if (nextTriggerDateTime!=null) {
            KairosSchedulerExecutorDTO jobDetails = new KairosSchedulerExecutorDTO();
            jobDetails.setEntityId(new BigInteger("316"));
            jobDetails.setOneTimeTriggerDateMillis(DateUtils.getMillisFromLocalDateTime(nextTriggerDateTime));
            sendReminderForEmail(jobDetails);
            //activityToSchedulerQueueService.pushToJobQueueForShiftReminder(999L, new BigInteger("5"), nextTriggerDateTime);
        }
    }
}