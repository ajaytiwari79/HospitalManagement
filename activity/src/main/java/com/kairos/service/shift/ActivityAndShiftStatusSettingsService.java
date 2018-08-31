package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusSettingsDTO;
import com.kairos.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.persistence.model.shift.ActivityAndShiftStatusSettings;
import com.kairos.persistence.repository.shift.ActivityAndShiftStatusSettingsRepository;
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
public class ActivityAndShiftStatusSettingsService extends MongoBaseService {

    @Inject
    private ActivityAndShiftStatusSettingsRepository activityAndShiftStatusSettingsRepository;
    @Inject
    private ExceptionService exceptionService;

    public ActivityAndShiftStatusSettingsDTO addActivityAndShiftStatusSettings(Long countryId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        ActivityAndShiftStatusSettings activityAndShiftStatusSettings=ObjectMapperUtils.copyPropertiesByMapper(activityAndShiftStatusSettingsDTO,ActivityAndShiftStatusSettings.class);
        activityAndShiftStatusSettings.setCountryId(countryId);
        save(activityAndShiftStatusSettings);
        activityAndShiftStatusSettingsDTO.setId(activityAndShiftStatusSettings.getId());
        return activityAndShiftStatusSettingsDTO;
    }

    public ActivityAndShiftStatusSettingsDTO updateActivityAndShiftStatusSettings(Long countryId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        Optional<ActivityAndShiftStatusSettings> activityAndShiftStatusSettings=activityAndShiftStatusSettingsRepository.findById(activityAndShiftStatusSettingsDTO.getId());
        if(!activityAndShiftStatusSettings.isPresent()){
            exceptionService.dataNotFoundException("settings.not.found",activityAndShiftStatusSettingsDTO.getId());
        }
        ObjectMapperUtils.copyProperties(activityAndShiftStatusSettingsDTO,activityAndShiftStatusSettings.get());
        activityAndShiftStatusSettings.get().setCountryId(countryId);
        save(activityAndShiftStatusSettings.get());
        return activityAndShiftStatusSettingsDTO;

    }


    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long countryId,BigInteger activityId){
          return activityAndShiftStatusSettingsRepository.getActivityAndShiftStatusSettingsGroupedByStatus(countryId,activityId);
    }

    // Endpoints methods for unit

    public ActivityAndShiftStatusSettingsDTO addActivityAndShiftStatusSettingForUnit(Long unitId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        ActivityAndShiftStatusSettings activityAndShiftStatusSettings=ObjectMapperUtils.copyPropertiesByMapper(activityAndShiftStatusSettingsDTO,ActivityAndShiftStatusSettings.class);
        activityAndShiftStatusSettings.setUnitId(unitId);
        save(activityAndShiftStatusSettings);
        activityAndShiftStatusSettingsDTO.setId(activityAndShiftStatusSettings.getId());
        return activityAndShiftStatusSettingsDTO;
    }


    public ActivityAndShiftStatusSettingsDTO updateActivityAndShiftStatusSettingsForUnit(Long unitId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        Optional<ActivityAndShiftStatusSettings> activityAndShiftStatusSettings=activityAndShiftStatusSettingsRepository.findById(activityAndShiftStatusSettingsDTO.getId());
        if(!activityAndShiftStatusSettings.isPresent()){
            exceptionService.dataNotFoundException("settings.not.found",activityAndShiftStatusSettingsDTO.getId());
        }
        ObjectMapperUtils.copyProperties(activityAndShiftStatusSettingsDTO,activityAndShiftStatusSettings);
        activityAndShiftStatusSettings.get().setUnitId(unitId);
        save(activityAndShiftStatusSettings.get());
        return activityAndShiftStatusSettingsDTO;

    }

    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatusForUnit(Long unitId,BigInteger activityId){
        return activityAndShiftStatusSettingsRepository.getActivityAndShiftStatusSettingsGroupedByStatusForUnit(unitId,activityId);
    }


}
