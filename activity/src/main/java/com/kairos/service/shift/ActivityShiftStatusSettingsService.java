package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.ActivityReminderSettings;
import com.kairos.dto.activity.activity.activity_tabs.communication_tab.FrequencySettings;
import com.kairos.dto.activity.shift.ActivityShiftStatusSettingsDTO;
import com.kairos.dto.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.enums.DurationType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.shift.ActivityShiftStatusSettingsRepository;
import com.kairos.scheduler_listener.ActivityToSchedulerQueueService;
import com.kairos.scheduler_listener.SchedulerToActivityQueueListener;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.kairos.enums.DurationType;

import static com.kairos.enums.DurationType.DAYS;
import static com.kairos.enums.DurationType.HOURS;
import static com.kairos.enums.DurationType.MINUTES;

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
        if (activity.getCommunicationActivityTab().isAllowCommunicationReminder()) {
            if (!activity.getCommunicationActivityTab().getActivityReminderSettings().isEmpty()) {

                LocalDateTime firstStartDate = LocalDateTime.now();
                ActivityReminderSettings firstSettings = activity.getCommunicationActivityTab().getActivityReminderSettings().get(0);
                firstStartDate = DateUtils.addDurationInLocalDateTime(firstStartDate, firstSettings.getSendReminder().getTimeValue(), firstSettings.getSendReminder().getDurationType(), 1);
                logger.info("first time date is {}", firstStartDate);
                LocalDateTime secondDate = LocalDateTime.now();
                for (ActivityReminderSettings current : activity.getCommunicationActivityTab().getActivityReminderSettings()) {
                    secondDate = DateUtils.addDurationInLocalDateTime(secondDate, current.getSendReminder().getTimeValue(), current.getSendReminder().getDurationType(), 1);
                    if (secondDate.isAfter(firstStartDate) || secondDate.isEqual(firstStartDate)) {
                        logger.info("I am running at {} ,{} ", firstStartDate, secondDate);
                        if (current.isRepeatAllowed()) {
                            secondDate = DateUtils.addDurationInLocalDateTime(LocalDateTime.now(), current.getRepeatReminder().getTimeValue(), current.getRepeatReminder().getDurationType(), 1);
                            logger.info("Inside to repeat", secondDate);
                        } else {
                            firstStartDate = secondDate;
                            logger.info("I need to repeat on {}", firstStartDate);
                        }
                    }
                    //activityToSchedulerQueueService.pushToJobQueueForShiftReminder(999L, new BigInteger("5"), activity.getCommunicationActivityTab().getActivityReminderSettings(), firstStartDate);


                }
            }
        }

    }
}