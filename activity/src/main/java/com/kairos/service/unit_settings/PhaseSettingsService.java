package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.unit_settings.PhaseSettingsDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.phase.PhaseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhaseSettingsService extends MongoBaseService {
    @Inject private PhaseSettingsRepository phaseSettingsRepository;
    @Inject private PhaseService phaseService;
    public List<PhaseSettingsDTO> getPhaseSettings(Long unitId){
        return phaseSettingsRepository.findAllByUnitIdAndDeletedFalse(unitId, Sort.by(Sort.Direction.ASC, "sequence"));
    }

    public List<PhaseSettingsDTO> updatePhaseSettings(Long unitId, List<PhaseSettingsDTO> phaseSettingsDTOS) {
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setUnitId(unitId);
        });
        List<PhaseSettings> phaseSettings = ObjectMapperUtils.copyPropertiesOfCollectionByMapper(phaseSettingsDTOS,PhaseSettings.class);
        save(phaseSettings);
        return phaseSettingsDTOS;
    }



    public boolean createDefaultPhaseSettings(Long unitId, List<Phase> phases){
        if (!Optional.ofNullable(phases).isPresent()){
            phases=ObjectMapperUtils.copyPropertiesOfCollectionByMapper(phaseService.getPhasesByUnit(unitId),Phase.class);
        }
        List<PhaseSettings> phaseSettings=new ArrayList<>();
        phases.forEach(phase -> {
            PhaseSettings phaseSetting=new PhaseSettings(phase.getId(),phase.getName(),phase.getDescription(),true,true,true,true,unitId,phase.getSequence());
            phaseSettings.add(phaseSetting);
        });
        phaseSettingsRepository.saveEntities(phaseSettings);
        return true;
    }
}
