package com.kairos.service.shift;
/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusSettingsDTO;
import com.kairos.persistence.model.shift.ActivityAndShiftStatusSettings;
import com.kairos.persistence.repository.shift.ActivityAndShiftStatusSettingsRepository;
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
public class ActivityAndShiftStatusSettingsService {

    @Inject
    private ActivityAndShiftStatusSettingsRepository activityAndShiftStatusSettingsRepository;
    @Inject
    private ExceptionService exceptionService;

    public ActivityAndShiftStatusSettingsDTO addActivityAndShiftStatusSettings(Long countryId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        ActivityAndShiftStatusSettings activityAndShiftStatusSettings=ObjectMapperUtils.copyPropertiesByMapper(activityAndShiftStatusSettingsDTO,ActivityAndShiftStatusSettings.class);
        activityAndShiftStatusSettings.setCountryId(countryId);
        activityAndShiftStatusSettingsRepository.save(activityAndShiftStatusSettings);
        activityAndShiftStatusSettingsDTO.setId(activityAndShiftStatusSettings.getId());
        return activityAndShiftStatusSettingsDTO;
    }

    public List<ActivityAndShiftStatusSettingsDTO> getAllActivityAndShiftStatusSettings(Long countryId){
        return activityAndShiftStatusSettingsRepository.findAllByCountryIdAndDeletedFalse(countryId);
    }

    public ActivityAndShiftStatusSettingsDTO updateActivityAndShiftStatusSettings(BigInteger id, Long countryId,ActivityAndShiftStatusSettingsDTO activityAndShiftStatusSettingsDTO){
        Optional<ActivityAndShiftStatusSettings> activityAndShiftStatusSettings=activityAndShiftStatusSettingsRepository.findById(id);
        if(!activityAndShiftStatusSettings.isPresent()){
            exceptionService.dataNotFoundException("",id);
        }
        ObjectMapperUtils.copyProperties(activityAndShiftStatusSettingsDTO,activityAndShiftStatusSettings);
        activityAndShiftStatusSettingsRepository.save(activityAndShiftStatusSettings.get());
        return activityAndShiftStatusSettingsDTO;

    }

    public boolean deleteActivityAndShiftStatusSettings(BigInteger id){
        return true;
    }


}
