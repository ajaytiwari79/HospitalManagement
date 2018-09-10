package com.kairos.service.break_settings;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.break_settings.BreakSettingAndActivitiesWrapper;
import com.kairos.activity.break_settings.BreakActivitiesDTO;
import com.kairos.activity.unit_settings.FlexibleTimeSettingDTO;
import com.kairos.activity.unit_settings.UnitSettingDTO;
import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.activity.break_settings.BreakSettingsDTO;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

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
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private UnitSettingRepository unitSettingRepository;

    public BreakSettingsDTO createBreakSettings(Long unitId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByDeletedFalseAndUnitIdAndShiftDurationInMinuteEquals(unitId, breakSettingsDTO.getShiftDurationInMinute());
        if (Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
        }
        breakSettings =ObjectMapperUtils.copyPropertiesByMapper(breakSettingsDTO,BreakSettings.class);
        breakSettings.setUnitId(unitId);
        save(breakSettings);
        breakSettingsDTO.setId(breakSettings.getId());
        return breakSettingsDTO;
    }

    public BreakSettingAndActivitiesWrapper getBreakSettings(Long unitId) {
        List<BreakActivitiesDTO> breakActivityDTOS =activityMongoRepository.getAllActivitiesGroupedByTimeType(unitId);
        Map<String,List<ActivityDTO>> timeTypeActivityMap= breakActivityDTOS.stream().collect(Collectors.toMap(k->k.getTimeType(), v->v.getActivities()));
        List<BreakSettingsDTO> breakSettings= breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdOrderByCreatedAtAsc(unitId);
        UnitSettingDTO unitSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(unitId);
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = new FlexibleTimeSettingDTO();
        if (unitSettingDTO != null) {
            flexibleTimeSettingDTO = unitSettingDTO.getFlexibleTimeSettings();
        }
        return new BreakSettingAndActivitiesWrapper(breakSettings,timeTypeActivityMap.get(WORKING_TYPE.name()),timeTypeActivityMap.get(NON_WORKING_TYPE.name()),flexibleTimeSettingDTO);
    }

    public Boolean removeBreakSettings(Long unitId, BigInteger breakSettingsId) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByIdAndDeletedFalseAndUnitId(breakSettingsId, unitId);
        if (!Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.breakSettings.notFound", breakSettingsId);
        }
        breakSettings.setDeleted(true);
        save(breakSettings);
        return true;
    }

    public BreakSettingsDTO updateBreakSettings(Long unitId, BigInteger breakSettingsId, BreakSettingsDTO breakSettingsDTO) {
        BreakSettings breakSettings = breakSettingMongoRepository.findByIdAndDeletedFalseAndUnitId(breakSettingsId, unitId);
        if (!Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.dataNotFoundByIdException("error.breakSettings.notFound", breakSettingsId);
        }
        if (!breakSettingsDTO.getShiftDurationInMinute().equals(breakSettings.getShiftDurationInMinute())) {
            BreakSettings breakSettingsFromDB = breakSettingMongoRepository.findByDeletedFalseAndUnitIdAndShiftDurationInMinuteEquals(unitId, breakSettingsDTO.getShiftDurationInMinute());
            if (Optional.ofNullable(breakSettingsFromDB).isPresent()) {
                exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
            }

        }
        breakSettings=ObjectMapperUtils.copyPropertiesByMapper(breakSettingsDTO,BreakSettings.class);
        breakSettings.setUnitId(unitId);
        save(breakSettings);
        return breakSettingsDTO;
    }



}
