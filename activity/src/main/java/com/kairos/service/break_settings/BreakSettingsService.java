package com.kairos.service.break_settings;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.break_settings.BreakSettingAndActivitiesWrapper;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.time_type.TimeTypeMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.PAID_BREAK;
import static com.kairos.constants.AppConstants.UNPAID_BREAK;
import static com.kairos.enums.shift.BreakPaymentSetting.PAID;

@Service
public class BreakSettingsService {

    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeTypeMongoRepository timeTypeMongoRepository;

    public BreakSettingsDTO createBreakSettings(Long countryId, Long expertiseId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(countryId, expertiseId);
        if (Optional.ofNullable(breakSettings).isPresent() && breakSettingsDTO.isPrimary()) {
            exceptionService.duplicateDataException(ERROR_BREAKSETTINGS_DUPLICATE);
        }
        breakSettings = new BreakSettings(countryId, breakSettingsDTO.getShiftDurationInMinute(), breakSettingsDTO.getBreakDurationInMinute(), expertiseId, breakSettingsDTO.getActivityId(),breakSettingsDTO.isPrimary(),breakSettingsDTO.isIncludeInPlanning());
        breakSettingMongoRepository.save(breakSettings);
        breakSettingsDTO.setId(breakSettings.getId());
        return breakSettingsDTO;
    }

    public BreakSettingAndActivitiesWrapper getBreakSettings(Long countryId, Long expertiseId) {
        Expertise expertise = userIntegrationService.getExpertise(countryId, expertiseId);
        if (!Optional.ofNullable(expertise).isPresent()) {
            exceptionService.duplicateDataException(ERROR_EXPERTISE_NOTFOUND);
        }
        String secondLevelTimeType=expertise.getBreakPaymentSetting().equals(PAID)?PAID_BREAK:UNPAID_BREAK;
        List<TimeType> timeTypes = timeTypeMongoRepository.findAllByDeletedFalseAndCountryIdAndTimeType(countryId,secondLevelTimeType );
        List<BigInteger> parentIds = timeTypes.stream().map(TimeType::getId).collect(Collectors.toList());
        List<ActivityDTO> activities = activityMongoRepository.findAllActivitiesByCountryIdAndTimeTypes(countryId, parentIds);

        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(expertiseId);
        return new BreakSettingAndActivitiesWrapper(breakSettings, activities);
    }

    public Boolean removeBreakSettings(BigInteger breakSettingsId) {
        breakSettingMongoRepository.safeDeleteById(breakSettingsId);
        return true;
    }

    public BreakSettingsDTO updateBreakSettings(Long countryId, Long expertiseId, BigInteger breakSettingsId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByIdAndDeletedFalse(breakSettingsId);
        if (!Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException(ERROR_BREAKSETTINGS_NOTFOUND, breakSettingsId);
        }
        BreakSettings breakSettingsFromDB = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndPrimaryTrue(countryId, expertiseId);
        if (Optional.ofNullable(breakSettingsFromDB).isPresent() && !breakSettingsFromDB.getId().equals(breakSettingsId) && breakSettingsDTO.isPrimary()) {
            exceptionService.duplicateDataException(ERROR_BREAKSETTINGS_DUPLICATE, breakSettingsDTO.getShiftDurationInMinute());
        }
        breakSettings.setIncludeInPlanning(breakSettingsDTO.isIncludeInPlanning());
        breakSettings.setPrimary(breakSettingsDTO.isPrimary());
        breakSettings.setBreakDurationInMinute(breakSettingsDTO.getBreakDurationInMinute());
        breakSettings.setActivityId(breakSettingsDTO.getActivityId());
        breakSettings.setShiftDurationInMinute(breakSettingsDTO.getShiftDurationInMinute());
        breakSettingMongoRepository.save(breakSettings);
        return breakSettingsDTO;
    }


    public BreakSettingAndActivitiesWrapper getBreakSettingsByExpertiseId(Long expertiseId) {
        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(expertiseId);
        List<ActivityDTO> activities = activityMongoRepository.findByDeletedFalseAndIdsIn(breakSettings.stream().map(BreakSettingsDTO::getActivityId).collect(Collectors.toSet()));
        return new BreakSettingAndActivitiesWrapper(breakSettings, activities);
    }

}
