package com.kairos.service.unit_settings;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.unit_settings.activity_configuration.AbsenceRankingDTO;
import com.kairos.persistence.model.unit_settings.AbsenceRankingSettings;
import com.kairos.persistence.repository.unit_settings.AbsenceRankingSettingsRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Service
public class AbsenceRankingSettingsService {
    @Inject
    private AbsenceRankingSettingsRepository absenceRankingSettingsRepository;

    public AbsenceRankingDTO saveAbsenceRankingSettings(AbsenceRankingDTO absenceRankingDTO){
        AbsenceRankingSettings absenceRankingSettings= ObjectMapperUtils.copyPropertiesByMapper(absenceRankingDTO,AbsenceRankingSettings.class);
        absenceRankingSettingsRepository.save(absenceRankingSettings);
        absenceRankingDTO.setId(absenceRankingSettings.getId());
        return absenceRankingDTO;
    }

    public AbsenceRankingDTO updateAbsenceRankingSettings(AbsenceRankingDTO absenceRankingDTO){
        AbsenceRankingSettings absenceRankingSettings= ObjectMapperUtils.copyPropertiesByMapper(absenceRankingDTO,AbsenceRankingSettings.class);
        absenceRankingSettingsRepository.save(absenceRankingSettings);
        return absenceRankingDTO;
    }

    public List<AbsenceRankingDTO> getAbsenceRankingSettings(){
        return absenceRankingSettingsRepository.getAbsenceRankingSettingsDeletedFalse();
    }

    public List<AbsenceRankingDTO> getAbsenceRankingSettings(Long expertiseId){
        return absenceRankingSettingsRepository.getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(expertiseId);
    }

    public boolean deleteAbsenceRankingSettings(BigInteger id){
        AbsenceRankingSettings absenceRankingSettings=absenceRankingSettingsRepository.findOne(id);
        absenceRankingSettings.setDeleted(true);
        absenceRankingSettingsRepository.save(absenceRankingSettings);
        return true;
    }
}
