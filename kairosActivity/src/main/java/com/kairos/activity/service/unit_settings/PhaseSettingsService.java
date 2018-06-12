package com.kairos.activity.service.unit_settings;

import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.persistence.model.phase.Phase;
import com.kairos.activity.persistence.model.unit_settings.PhaseSettings;
import com.kairos.activity.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.phase.PhaseService;
import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.response.dto.web.unit_settings.PhaseSettingsDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhaseSettingsService extends MongoBaseService {
    @Inject private PhaseSettingsRepository phaseSettingsRepository;
    @Inject private PhaseService phaseService;
    public List<PhaseSettingsDTO> getPhaseSettings(Long unitId){
        return phaseSettingsRepository.findAllByUnitIdAndDeletedFalse(unitId);
    }

    public List<PhaseSettingsDTO> updatePhaseSettings(Long unitId, List<PhaseSettingsDTO> phaseSettingsDTOS) {
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setUnitId(unitId);
        });
        save(ObjectMapperUtils.copyProperties(phaseSettingsDTOS,PhaseSettings.class));
        return phaseSettingsDTOS;
    }

    public boolean createDefaultPhaseSettings(Long unitId, List<Phase> phases){
        List<PhaseSettings> phaseSettings=new ArrayList<>();
        phases.forEach(phase -> {
            PhaseSettings phaseSetting=new PhaseSettings(phase.getId(),phase.getName(),phase.getDescription(),true,true,true,true,unitId);
            phaseSettings.add(phaseSetting);
        });
        save(phaseSettings);
        return true;
    }
}
