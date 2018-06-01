package com.kairos.activity.service.unit_settings;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.persistence.model.unit_settings.PhaseSettings;
import com.kairos.activity.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.unit_settings.PhaseSettingsDTO;

import javax.inject.Inject;
import java.util.List;

public class PhaseSettingsService extends MongoBaseService {
    @Inject private PhaseSettingsRepository phaseSettingsRepository;
    @Inject private PhaseService phaseService;
    public List<PhaseSettingsDTO> getPhaseSettings(Long unitId){
        return phaseSettingsRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }

    public List<PhaseSettingsDTO> updatePhaseSettings(Long unitId, List<PhaseSettingsDTO> phaseSettingsDTOS) {
        save(ObjectMapperUtils.copyProperties(phaseSettingsDTOS,PhaseSettings.class));
        return phaseSettingsDTOS;
    }

    public void createDefaultPhaseSettings(Long unitId){
        List<PhaseDTO> unitPhases=phaseService.getPhasesByUnit(unitId);

    }
}
