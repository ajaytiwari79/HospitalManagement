package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.unit_settings.UnitGeneralSettingDTO;
import com.kairos.enums.TimeBankLimitsType;
import com.kairos.persistence.model.unit_settings.UnitGeneralSetting;
import com.kairos.persistence.repository.unit_settings.UnitGeneralSettingRepository;
import com.kairos.service.wta.WorkTimeAgreementService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static com.kairos.commons.utils.ObjectUtils.isNull;

@Service
public class UnitGeneralSettingService {

    @Inject
    private UnitGeneralSettingRepository unitGeneralSettingRepository;

    @Inject @Lazy
    private WorkTimeAgreementService workTimeAgreementService;

    public UnitGeneralSettingDTO createGeneralSetting(UnitGeneralSettingDTO unitGeneralSettingDTO){
        UnitGeneralSetting unitGeneralSetting = ObjectMapperUtils.copyPropertiesByMapper(unitGeneralSettingDTO, UnitGeneralSetting.class);
        unitGeneralSettingRepository.save(unitGeneralSetting);
        unitGeneralSettingDTO.setId(unitGeneralSetting.getId());
        return unitGeneralSettingDTO;
    }

    public UnitGeneralSettingDTO updateGeneralSetting(Long unitId, UnitGeneralSettingDTO unitGeneralSettingDTO){
        UnitGeneralSetting unitGeneralSetting = unitGeneralSettingRepository.findByUnitId(unitId);
        if(isNull(unitGeneralSetting)){
            return createGeneralSetting(new UnitGeneralSettingDTO(null,unitId, unitGeneralSettingDTO.getTimeBankLimitsType()));
        }
        unitGeneralSetting.setTimeBankLimitsType(unitGeneralSettingDTO.getTimeBankLimitsType());
        unitGeneralSettingRepository.save(unitGeneralSetting);
        workTimeAgreementService.createWtaLineOnUpdateUnitSetting(unitId);
        return unitGeneralSettingDTO;
    }

    public UnitGeneralSettingDTO getGeneralSetting(Long unitId){
        UnitGeneralSetting unitGeneralSetting = unitGeneralSettingRepository.findByUnitId(unitId);
        if(isNull(unitGeneralSetting)){
            return createGeneralSetting(new UnitGeneralSettingDTO(null,unitId, TimeBankLimitsType.FIXED_VALUE_IN_HOURS));
        }
        return ObjectMapperUtils.copyPropertiesByMapper(unitGeneralSetting, UnitGeneralSettingDTO.class);
    }

}
