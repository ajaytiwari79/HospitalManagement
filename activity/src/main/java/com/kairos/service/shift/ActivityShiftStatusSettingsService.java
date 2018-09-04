package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityShiftStatusSettingsDTO;
import com.kairos.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import com.kairos.persistence.repository.shift.ActivityShiftStatusSettingsRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
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



    public ActivityShiftStatusSettingsDTO addActivityAndShiftStatusSetting(Long unitId, ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO){
        ActivityShiftStatusSettings activityShiftStatusSettings =ObjectMapperUtils.copyPropertiesByMapper(activityShiftStatusSettingsDTO,ActivityShiftStatusSettings.class);
        activityShiftStatusSettings.setUnitId(unitId);
        save(activityShiftStatusSettings);
        activityShiftStatusSettingsDTO.setId(activityShiftStatusSettings.getId());
        return activityShiftStatusSettingsDTO;
    }


    public ActivityShiftStatusSettingsDTO updateActivityAndShiftStatusSettings(Long unitId, ActivityShiftStatusSettingsDTO activityShiftStatusSettingsDTO){
        Optional<ActivityShiftStatusSettings> activityAndShiftStatusSettings=activityAndShiftStatusSettingsRepository.findById(activityShiftStatusSettingsDTO.getId());
        if(!activityAndShiftStatusSettings.isPresent()){
            exceptionService.dataNotFoundException("settings.not.found", activityShiftStatusSettingsDTO.getId());
        }
        ObjectMapperUtils.copyProperties(activityShiftStatusSettingsDTO,activityAndShiftStatusSettings);
        activityAndShiftStatusSettings.get().setUnitId(unitId);
        save(activityAndShiftStatusSettings.get());
        return activityShiftStatusSettingsDTO;

    }

    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long unitId, BigInteger activityId){
        return activityAndShiftStatusSettingsRepository.getActivityAndShiftStatusSettingsGroupedByStatus(unitId,activityId);
    }


}
