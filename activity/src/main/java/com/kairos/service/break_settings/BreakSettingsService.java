package com.kairos.service.break_settings;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.break_settings.BreakSettingAndActivitiesWrapper;
import com.kairos.dto.activity.break_settings.BreakSettingsResponseDTO;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BreakSettingsService extends MongoBaseService {

    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private GenericIntegrationService genericIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;

    public BreakSettingsDTO createBreakSettings(Long countryId, Long expertiseId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndShiftDurationInMinuteEquals(countryId, expertiseId, breakSettingsDTO.getShiftDurationInMinute());
        if (Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
        }
        breakSettings = new BreakSettings(countryId,breakSettingsDTO.getShiftDurationInMinute(),breakSettingsDTO.getBreakDurationInMinute(),expertiseId,breakSettingsDTO.getActivityId());
        save(breakSettings);
        breakSettingsDTO.setId(breakSettings.getId());
        return breakSettingsDTO;
    }

    public BreakSettingAndActivitiesWrapper getBreakSettings(Long countryId, Long expertiseId) {
        Expertise expertise = genericIntegrationService.getExpertise(countryId, expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.duplicateDataException("error.expertise.notfound");
        }
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByDeletedFalseAndCountryIdAndTimeType(countryId, expertise.getBreakPaymentSetting().toString());
        List<BigInteger> parentIds=timeTypes.stream().map(TimeType::getId).collect(Collectors.toList());
        List<TimeType> childTimeType = timeTypeMongoRepository.findAllChildTimeTypeByParentId(parentIds);
        parentIds.addAll(childTimeType.stream().map(TimeType::getId).collect(Collectors.toList()));
        List<ActivityDTO> activities = activityMongoRepository.findAllActivitiesByCountryIdAndTimeTypes(countryId, parentIds);

        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(expertiseId);
        // TODO VIPUL FIX
        /*UnitSettingDTO unitSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(unitId);
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = new FlexibleTimeSettingDTO();
        if (unitSettingDTO != null) {
            flexibleTimeSettingDTO = unitSettingDTO.getGlideTimeSettings();
        }
        */
        return new BreakSettingAndActivitiesWrapper(breakSettings, activities, null);
    }

    public Boolean removeBreakSettings(BigInteger breakSettingsId) {
        breakSettingMongoRepository.safeDeleteById(breakSettingsId);
        return true;
    }

    public BreakSettingsDTO updateBreakSettings(Long countryId, Long expertiseId, BigInteger breakSettingsId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByIdAndDeletedFalse(breakSettingsId);
        if (!Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.breakSettings.notFound", breakSettingsId);
        }
        if (!breakSettingsDTO.getShiftDurationInMinute().equals(breakSettings.getShiftDurationInMinute())) {
            BreakSettings breakSettingsFromDB = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndShiftDurationInMinuteEquals(countryId, expertiseId, breakSettingsDTO.getShiftDurationInMinute());
            if (Optional.ofNullable(breakSettingsFromDB).isPresent()) {
                exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
            }

        }
        breakSettings.setBreakDurationInMinute(breakSettingsDTO.getBreakDurationInMinute());
        breakSettings.setActivityId(breakSettingsDTO.getActivityId());
        breakSettings.setShiftDurationInMinute(breakSettingsDTO.getShiftDurationInMinute());
        save(breakSettings);
        return breakSettingsDTO;
    }


    public List<BreakSettingsResponseDTO> getBreakSettingsByExpertiseId(Long expertiseId) {
       return  breakSettingMongoRepository.findAllBreakSettingsByExpertise(expertiseId);
    }

}
