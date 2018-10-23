package com.kairos.service.break_settings;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.break_settings.BreakSettingAndActivitiesWrapper;
import com.kairos.dto.activity.break_settings.BreakActivitiesDTO;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.commons.utils.ObjectMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kairos.enums.TimeTypes.NON_WORKING_TYPE;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;

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

    public BreakSettingsDTO createBreakSettings(Long countryId, Long expertiseId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndShiftDurationInMinuteEquals(countryId, expertiseId, breakSettingsDTO.getShiftDurationInMinute());
        if (Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
        }
        breakSettings = ObjectMapperUtils.copyPropertiesByMapper(breakSettingsDTO, BreakSettings.class);
        breakSettings.setCountryId(countryId);
        breakSettings.setExpertiseId(expertiseId);
        save(breakSettings);
        breakSettingsDTO.setId(breakSettings.getId());
        return breakSettingsDTO;
    }

    public BreakSettingAndActivitiesWrapper getBreakSettings(Long countryId, Long expertiseId) {

        List<BreakActivitiesDTO> breakActivityDTOS = activityMongoRepository.getAllActivitiesGroupedByTimeType(countryId);

        Expertise expertise = genericIntegrationService.getExpertise(countryId, expertiseId);
        Map<String, List<ActivityDTO>> timeTypeActivityMap = breakActivityDTOS.stream().collect(Collectors.toMap(k -> k.getTimeType(), v -> v.getActivities()));
        if (expertise.getBreakPaymentSetting().equals(BreakPaymentSetting.PAID)){
            activityMongoRepository.findAllActivitiesByCountryIdAndTimeTypes(countryId,expertise.getBreakPaymentSetting());
        }else {

        }
        List<BreakSettingsDTO> breakSettings = breakSettingMongoRepository.findAllByDeletedFalseAndExpertiseIdOrderByCreatedAtAsc(expertiseId);
        // TODO VIPUL FIX
        /*UnitSettingDTO unitSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(unitId);
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = new FlexibleTimeSettingDTO();
        if (unitSettingDTO != null) {
            flexibleTimeSettingDTO = unitSettingDTO.getFlexibleTimeSettings();
        }
        */
        return new BreakSettingAndActivitiesWrapper(breakSettings, timeTypeActivityMap.get(WORKING_TYPE.name()), timeTypeActivityMap.get(NON_WORKING_TYPE.name()), null);
    }

    public Boolean removeBreakSettings(BigInteger breakSettingsId) {
        breakSettingMongoRepository.safeDeleteById(breakSettingsId);
        return true;
    }

    public BreakSettingsDTO updateBreakSettings(Long countryId, Long expertiseId,BigInteger breakSettingsId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByIdAndDeletedFalse(breakSettingsId);
        if (!Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.breakSettings.notFound", breakSettingsId);
        }
        if (!breakSettingsDTO.getShiftDurationInMinute().equals(breakSettings.getShiftDurationInMinute())) {
            BreakSettings breakSettingsFromDB = breakSettingMongoRepository.findByDeletedFalseAndCountryIdAndExpertiseIdAndShiftDurationInMinuteEquals(countryId,expertiseId, breakSettingsDTO.getShiftDurationInMinute());
            if (Optional.ofNullable(breakSettingsFromDB).isPresent()) {
                exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
            }

        }
        breakSettings = ObjectMapperUtils.copyPropertiesByMapper(breakSettingsDTO, BreakSettings.class);
        save(breakSettings);
        return breakSettingsDTO;
    }


}
