package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.unit_settings.PhaseSettingsDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.phase.PhaseService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhaseSettingsService extends MongoBaseService {
    @Inject private PhaseSettingsRepository phaseSettingsRepository;
    @Inject private PhaseService phaseService;
    public List<PhaseSettingsDTO> getPhaseSettings(Long unitId){
        List<Phase> phases = phaseService.getPhaseByUnitId(unitId);
        Map<BigInteger,Map<String, TranslationInfo>> phaseTranslationMap =phases.stream().collect(Collectors.toMap(Phase::getId,Phase::getTranslations));
        List<PhaseSettingsDTO> phaseSettingsDTOS = phaseSettingsRepository.findAllByUnitIdAndDeletedFalse(unitId, Sort.by(Sort.Direction.ASC, "sequence"));
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setTranslations(phaseTranslationMap.getOrDefault(phaseSettingsDTO.getPhaseId(),new HashMap<>()));
        });
        return phaseSettingsDTOS;
    }

    public List<PhaseSettingsDTO> updatePhaseSettings(Long unitId, List<PhaseSettingsDTO> phaseSettingsDTOS) {
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setUnitId(unitId);
        });
        List<PhaseSettings> phaseSettings = ObjectMapperUtils.copyCollectionPropertiesByMapper(phaseSettingsDTOS,PhaseSettings.class);
        save(phaseSettings);
        return phaseSettingsDTOS;
    }



    public boolean createDefaultPhaseSettings(Long unitId, List<Phase> phases){
        if (!Optional.ofNullable(phases).isPresent()){
            phases=ObjectMapperUtils.copyCollectionPropertiesByMapper(phaseService.getPhasesByUnit(unitId),Phase.class);
        }
        List<PhaseSettings> phaseSettings=new ArrayList<>();
        phases.forEach(phase -> {
            PhaseSettings phaseSetting=new PhaseSettings(phase.getId(),phase.getName(),phase.getDescription(),true,true,true,true,unitId,phase.getSequence());
            phaseSettings.add(phaseSetting);
        });
        phaseSettingsRepository.saveEntities(phaseSettings);
        return true;
    }

    public Map<String, TranslationInfo> updatePhaseSettingTranslations(Long unitId, BigInteger phaseId,Map<String,TranslationInfo> translations){
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(unitId,phaseId);
        phaseSettings.setTranslations(translations);
        phaseSettingsRepository.save(phaseSettings);
        return phaseSettings.getTranslations();
    }
}
