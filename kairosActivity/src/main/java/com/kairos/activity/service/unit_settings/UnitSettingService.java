package com.kairos.activity.service.unit_settings;


import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.constants.AppConstants;
import com.kairos.activity.custom_exception.DataNotFoundByIdException;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.unit_settings.UnitAgeSetting;
import com.kairos.activity.persistence.model.unit_settings.UnitSetting;
import com.kairos.activity.persistence.repository.unit_settings.UnitAgeSettingMongoRepository;
import com.kairos.activity.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.unit_settings.OpenShiftPhase;
import com.kairos.response.dto.web.unit_settings.OpenShiftPhaseSetting;
import com.kairos.response.dto.web.unit_settings.UnitAgeSettingDTO;
import com.kairos.response.dto.web.unit_settings.UnitSettingDTO;
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
    @Inject private UnitSettingRepository unitSettingRepository;
    @Inject private PhaseService phaseService;

    public UnitAgeSetting createDefaultNightWorkerSettings(Long unitId) {
        UnitAgeSetting unitAgeSetting = new UnitAgeSetting(AppConstants.YOUNGER_AGE,AppConstants.OLDER_AGE, unitId);
        save(unitAgeSetting);
        return unitAgeSetting;
    }

    public UnitAgeSettingDTO getUnitAgeSettings(Long unitId){
        UnitAgeSetting unitAgeSetting = unitAgeSettingMongoRepository.findByUnit(unitId);
        if(!Optional.ofNullable(unitAgeSetting).isPresent()){
            unitAgeSetting =  createDefaultNightWorkerSettings(unitId);
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

    public List<UnitSettingDTO> getOpenShiftPhaseSettings(Long unitId){
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

    public boolean createDefaultOpenShiftPhaseSettings(Long unitId){
        List<UnitSettingDTO> openShiftPhaseSettings=unitSettingRepository.getOpenShiftPhaseSettings(unitId);
        if(openShiftPhaseSettings.size()>0){
            exceptionService.actionNotPermittedException("openShift.already.exist",unitId);
        }
        List<PhaseDTO> phases=phaseService.getPhasesByUnit(unitId);
        if(Optional.ofNullable(phases).isPresent()) {
            List<OpenShiftPhase> openShiftPhases = new ArrayList<>();
            phases.forEach(phase -> {
                OpenShiftPhase openShiftPhase = new OpenShiftPhase(phase.getId(), phase.getName(), true);
                openShiftPhases.add(openShiftPhase);
            });
            OpenShiftPhaseSetting openShiftPhaseSetting = new OpenShiftPhaseSetting(4, openShiftPhases);
            UnitSetting unitSetting = new UnitSetting(openShiftPhaseSetting, unitId);
            save(unitSetting);
            return true;
        }
        return false;

    }
}
