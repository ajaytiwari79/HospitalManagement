package com.kairos.persistence.repository.break_settings;

import com.kairos.dto.activity.break_settings.BreakSettingsResponseDTO;

import java.util.List;

public interface CustomBreakSettingsMongoRepository  {
   List<BreakSettingsResponseDTO> findAllBreakSettingsByExpertise(Long expertiseId);

}
