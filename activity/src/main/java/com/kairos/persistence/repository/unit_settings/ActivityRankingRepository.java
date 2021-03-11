package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface ActivityRankingRepository extends MongoBaseRepository<ActivityRanking,BigInteger> {

    List<ActivityRankingDTO> getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(Long expertiseId);

    List<ActivityRankingDTO> getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(Long expertiseId, Boolean published);

    List<ActivityRankingDTO> getAbsenceRankingSettingsByDeletedFalse();

    ActivityRanking findByDraftIdAndDeletedFalse(BigInteger draftId);

    ActivityRanking findTopByExpertiseIdAndDeletedFalseOrderByStartDateDesc(Long expertiseId);

    List<ActivityRankingDTO> getAbsenceRankingSettingsByUnitIdAndDeletedFalse(Long unitId);

    //List<ActivityRanking> findAllByExpertiseIdInAndDeletedFalseAndEndDateGreaterThanEquals(List<Long> expertiseIds, LocalDate startDate);

}
