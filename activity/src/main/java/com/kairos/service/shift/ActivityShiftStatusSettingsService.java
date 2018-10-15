package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.dto.activity.shift.ActivityShiftStatusSettingsDTO;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
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
import java.util.List;
import java.util.Optional;

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

}