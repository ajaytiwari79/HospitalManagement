package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.AbsenceRankingDTO;
import com.kairos.persistence.model.unit_settings.AbsenceRankingSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AbsenceRankingSettingsRepository extends MongoBaseRepository<AbsenceRankingSettings,BigInteger> {

    List<AbsenceRankingDTO> getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(Long expertiseId);

    List<AbsenceRankingDTO> getAbsenceRankingSettingsDeletedFalse();

    AbsenceRankingSettings findByDraftIdAndDeletedFalse(BigInteger draftId);

    AbsenceRankingSettings findByExpertiseIdAndDeletedFalseOrderByStartDateDescLimitOne(Long expertiseId);

}
