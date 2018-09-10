package com.kairos.service.unit_settings;


import com.kairos.activity.unit_settings.*;
import com.kairos.constants.AppConstants;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.FlexibleTimeSettings;
import com.kairos.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.persistence.model.unit_settings.UnitSetting;
import com.kairos.persistence.repository.unit_settings.UnitAgeSettingMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.phase.PhaseService;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitSettingService extends MongoBaseService {

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private UnitAgeSettingMongoRepository unitAgeSettingMongoRepository;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private PhaseService phaseService;

    public UnitAgeSetting createDefaultNightWorkerSettings(Long unitId) {
        UnitAgeSetting unitAgeSetting = new UnitAgeSetting(AppConstants.YOUNGER_AGE, AppConstants.OLDER_AGE, unitId);
        save(unitAgeSetting);
        return unitAgeSetting;
    }

    public UnitAgeSettingDTO getUnitAgeSettings(Long unitId) {
        UnitAgeSetting unitAgeSetting = unitAgeSettingMongoRepository.findByUnit(unitId);
        if (!Optional.ofNullable(unitAgeSetting).isPresent()) {
            unitAgeSetting = createDefaultNightWorkerSettings(unitId);
        }
        return ObjectMapperUtils.copyPropertiesByMapper(unitAgeSetting, UnitAgeSettingDTO.class);
    }

    public UnitAgeSettingDTO updateUnitAgeSettings(Long unitId, UnitAgeSettingDTO unitSettingsDTO) {
        UnitAgeSetting unitAgeSetting = unitAgeSettingMongoRepository.findByUnit(unitId);
        if (!Optional.ofNullable(unitAgeSetting).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.ageSetting.notFound", unitId);
        }
        unitAgeSetting.setYounger(unitSettingsDTO.getYounger());
        unitAgeSetting.setOlder(unitSettingsDTO.getOlder());

        save(unitAgeSetting);
        return unitSettingsDTO;
    }

    public List<UnitSettingDTO> getOpenShiftPhaseSettings(Long unitId) {
        return unitSettingRepository.getOpenShiftPhaseSettings(unitId);
    }

    public UnitSettingDTO updateOpenShiftPhaseSettings(Long unitId, BigInteger unitSettingsId, UnitSettingDTO unitSettingsDTO) {
        Optional<UnitSetting> unitSetting = unitSettingRepository.findById(unitSettingsId);
        if (!unitSetting.isPresent()) {
            exceptionService.dataNotFoundByIdException("message.unit.setting.notFound", unitSettingsId);
        }
        unitSetting.get().setUnitId(unitId);
        unitSetting.get().setOpenShiftPhaseSetting(unitSettingsDTO.getOpenShiftPhaseSetting());
        save(unitSetting.get());
        return unitSettingsDTO;
    }

    public boolean createDefaultOpenShiftPhaseSettings(Long unitId, List<Phase> phases) {
        if (!Optional.ofNullable(phases).isPresent()) {
            phases = ObjectMapperUtils.copyPropertiesOfListByMapper(phaseService.getPhasesByUnit(unitId), Phase.class);
        }
        List<UnitSettingDTO> openShiftPhaseSettings = unitSettingRepository.getOpenShiftPhaseSettings(unitId);
        if (openShiftPhaseSettings.size() > 0) {
            exceptionService.actionNotPermittedException("openShift.already.exist", unitId);
        }

        if (Optional.ofNullable(phases).isPresent()) {
            List<OpenShiftPhase> openShiftPhases = new ArrayList<>();
            phases.forEach(phase -> {
                OpenShiftPhase openShiftPhase = new OpenShiftPhase(phase.getId(), phase.getName(), false);
                openShiftPhases.add(openShiftPhase);
            });
            OpenShiftPhaseSetting openShiftPhaseSetting = new OpenShiftPhaseSetting(4, openShiftPhases);
            UnitSetting unitSetting = new UnitSetting(openShiftPhaseSetting, unitId);
            save(unitSetting);
            return true;
        }
        return false;

    }

    public FlexibleTimeSettingDTO getFlexibleTime(Long unitId) {
        UnitSettingDTO unitSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(unitId);
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = new FlexibleTimeSettingDTO();
        if (unitSettingDTO != null) {
            flexibleTimeSettingDTO = unitSettingDTO.getFlexibleTimeSettings();
        }
        return flexibleTimeSettingDTO;
    }

    public FlexibleTimeSettingDTO updateFlexibleTime(Long unitId, FlexibleTimeSettingDTO flexibleTimeSettingDTO) {
        UnitSetting unitSetting = unitSettingRepository.findByUnitIdAndDeletedFalse(unitId);
        if (unitSetting == null) {
            exceptionService.dataNotFoundException("message.unit.setting.notFound");
        }
        FlexibleTimeSettings flexibleTimeSettings = ObjectMapperUtils.copyPropertiesByMapper(flexibleTimeSettingDTO, FlexibleTimeSettings.class);
        unitSetting.setUnitId(unitId);
        unitSetting.setFlexibleTimeSettings(flexibleTimeSettings);
        save(unitSetting);
        return flexibleTimeSettingDTO;
    }
}
