package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.enums.PriorityFor;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ActivityRankingRepository extends MongoBaseRepository<ActivityRanking,BigInteger> {

    List<ActivityRankingDTO> getActivityRankingByExpertiseIdAndDeletedFalse(Long expertiseId);

    List<ActivityRankingDTO> getActivityRankingByExpertiseIdAndPublishedAndDeletedFalse(Long expertiseId, Boolean published);

    List<ActivityRankingDTO> getActivityRankingByDeletedFalse();

    ActivityRanking findByDraftIdAndPriorityForAndDeletedFalse(BigInteger draftId, PriorityFor priorityFor);

    ActivityRanking findTopByExpertiseIdAndPriorityForAndDeletedFalseOrderByStartDateDesc(Long expertiseId, PriorityFor priorityFor);

    List<ActivityRankingDTO> getActivityRankingByPublishedAndUnitIdAndDeletedFalse(Boolean published, Long unitId);

    List<ActivityRankingDTO> getActivityRankingByUnitIdAndDeletedFalse(Long unitId);

}
