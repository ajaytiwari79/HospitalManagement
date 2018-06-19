package com.kairos.activity.service.break_settings;

import com.kairos.activity.persistence.model.break_settings.BreakSettings;
import com.kairos.activity.persistence.repository.break_settings.BreakSettingMongoRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.break_settings.BreakSettingsDTO;
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
        breakSettings = breakSettingMongoRepository.findFirstByDeletedFalseAndUnitIdOrderByCreatedAtDesc(unitId);
        if (Optional.ofNullable(breakSettings).isPresent() && breakSettings.getShiftDurationInMinute() > breakSettingsDTO.getShiftDurationInMinute()) {
            exceptionService.duplicateDataException("error.breakSettings.greaterThan", DateUtils.getTimeFromMinuteLong(breakSettings.getShiftDurationInMinute()));
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
            List<BreakSettings> breakSettingsList = breakSettingMongoRepository.findAllByUnitIdAndDeletedFalseOrderByCreatedAtAsc(unitId);
            for (int i = 0; i < breakSettingsList.size(); i++) {
                // current break settings
                if (breakSettings.getId().equals(breakSettingsList.get(i).getId())) {
                    // validate previous
                    if (i != 0 && (breakSettingsList.get(i - 1).getShiftDurationInMinute() >= breakSettingsDTO.getShiftDurationInMinute())) {
                        exceptionService.actionNotPermittedException("error.breakSettings.greaterThan", DateUtils.getTimeFromMinuteLong(breakSettingsList.get(i - 1).getShiftDurationInMinute()));
                    }
                    if (i != breakSettingsList.size() - 1 && breakSettingsList.get(i + 1).getShiftDurationInMinute() <= breakSettingsDTO.getShiftDurationInMinute()) {
                        exceptionService.actionNotPermittedException("error.breakSettings.lessThan", DateUtils.getTimeFromMinuteLong(breakSettingsList.get(i + 1).getShiftDurationInMinute()));
                    }


                }
            }
        }
        breakSettings.setShiftDurationInMinute(breakSettingsDTO.getShiftDurationInMinute());
        breakSettings.setBreakDurationInMinute(breakSettingsDTO.getBreakDurationInMinute());
        breakSettings.setNumberOfBreaks(breakSettingsDTO.getNumberOfBreaks());
        save(breakSettings);

        return breakSettingsDTO;
    }

}
