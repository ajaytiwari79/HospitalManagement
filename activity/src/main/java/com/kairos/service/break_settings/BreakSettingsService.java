package com.kairos.service.break_settings;

import com.kairos.persistence.model.break_settings.BreakSettings;
import com.kairos.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.activity.break_settings.BreakSettingsDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class BreakSettingsService extends MongoBaseService {

    @Inject
    private BreakSettingMongoRepository breakSettingMongoRepository;
    @Inject
    private ExceptionService exceptionService;

    public BreakSettingsDTO createBreakSettings(Long unitId, BreakSettingsDTO breakSettingsDTO) {

        BreakSettings breakSettings = breakSettingMongoRepository.findByDeletedFalseAndUnitIdAndShiftDurationInMinuteEquals(unitId, breakSettingsDTO.getShiftDurationInMinute());
        if (Optional.ofNullable(breakSettings).isPresent()) {
            exceptionService.duplicateDataException("error.breakSettings.duplicate", breakSettingsDTO.getShiftDurationInMinute());
        }
        breakSettings = new BreakSettings(unitId, breakSettingsDTO.getShiftDurationInMinute(), breakSettingsDTO.getBreakDurationInMinute(), breakSettingsDTO.getNumberOfBreaks());
        save(breakSettings);
        breakSettingsDTO.setId(breakSettings.getId());
        return breakSettingsDTO;
    }

    public List<BreakSettingsDTO> getBreakSettings(Long unitId) {
        return breakSettingMongoRepository.findAllByDeletedFalseAndUnitIdOrderByCreatedAtAsc(unitId);
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
        breakSettings.setShiftDurationInMinute(breakSettingsDTO.getShiftDurationInMinute());
        breakSettings.setBreakDurationInMinute(breakSettingsDTO.getBreakDurationInMinute());
        breakSettings.setNumberOfBreaks(breakSettingsDTO.getNumberOfBreaks());
        save(breakSettings);
        return breakSettingsDTO;
    }

}
