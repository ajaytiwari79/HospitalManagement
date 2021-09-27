package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.unit_settings.PhaseSettingsDTO;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.persistence.repository.unit_settings.PhaseSettingsRepository;
import com.kairos.service.phase.PhaseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhaseSettingsService {
    @Inject private PhaseSettingsRepository phaseSettingsRepository;
    @Inject private PhaseService phaseService;

    //@Cacheable(value = "getPhaseSettings", key = "#unitId", cacheManager = "cacheManager")
    public List<PhaseSettingsDTO> getPhaseSettings(Long unitId){
        List<PhaseSettingsDTO> phaseSettingsDTOS = phaseSettingsRepository.findAllByUnitIdAndDeletedFalse(unitId, Sort.by(Sort.Direction.ASC, "sequence"));
        Map<BigInteger,PhaseDTO> phaseDTOMap = phaseService.getPhasesByUnit(unitId).stream().collect(Collectors.toMap(k->k.getId(), v->v));
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setTranslations(phaseDTOMap.get(phaseSettingsDTO.getPhaseId()).getTranslations());
        });
        return phaseSettingsDTOS;
    }

    @Caching(evict = {
            @CacheEvict(value = "getPhaseSettingsByUnitIdAndPhaseId",allEntries = true),
            @CacheEvict(value = "getPhaseSettings", key = "#unitId")
    })
    public List<PhaseSettingsDTO> updatePhaseSettings(Long unitId, List<PhaseSettingsDTO> phaseSettingsDTOS) {
        phaseSettingsDTOS.forEach(phaseSettingsDTO -> {
            phaseSettingsDTO.setUnitId(unitId);
        });
        List<PhaseSettings> phaseSettings = ObjectMapperUtils.copyCollectionPropertiesByMapper(phaseSettingsDTOS,PhaseSettings.class);
        phaseSettingsRepository.saveEntities(phaseSettings);
        return phaseSettingsDTOS;
    }


    @Caching(evict = {
            @CacheEvict(value = "getPhaseSettingsByUnitIdAndPhaseId",allEntries = true),
            @CacheEvict(value = "getPhaseSettings", key = "#unitId")
    })
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

    @Caching(evict = {
            @CacheEvict(value = "getPhaseSettingsByUnitIdAndPhaseId",key = "#phaseId"),
            @CacheEvict(value = "getPhaseSettings", allEntries = true)
    })
    public Map<String, TranslationInfo> updatePhaseSettingTranslations(Long unitId, BigInteger phaseId,Map<String,TranslationInfo> translations){
        PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(unitId,phaseId);
        phaseSettings.setTranslations(translations);
        phaseSettingsRepository.save(phaseSettings);
        return phaseSettings.getTranslations();
    }
}
