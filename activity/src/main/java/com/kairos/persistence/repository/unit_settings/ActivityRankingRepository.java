package com.kairos.persistence.repository.unit_settings;

import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityRankingDTO;
import com.kairos.persistence.model.unit_settings.ActivityRanking;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface ActivityRankingRepository extends MongoBaseRepository<ActivityRanking,BigInteger> {


    List<ActivityRanking> getAbsenceRankingSettingsByExpertiseIdAndDeletedFalse(Long expertiseId);

    @Query("{expertiseId:?0, deleted:false, published:true, '$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
    List<ActivityRanking> getAbsenceRankingSettings(Long expertiseId, LocalDate endDate);

    @Query("{unitId:?0, deleted:false, published:true, '$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
    List<ActivityRanking> getPresenceRankingSettings(Long unitId, LocalDate endDate);

    List<ActivityRanking> getAbsenceRankingSettingsByExpertiseIdAndPublishedAndDeletedFalse(Long expertiseId, Boolean published);

    List<ActivityRankingDTO> getActivityRankingByExpertiseIdAndDeletedFalse(Long expertiseId);

    List<ActivityRankingDTO> getActivityRankingByExpertiseIdAndPublishedAndDeletedFalse(Long expertiseId, Boolean published);

    List<ActivityRankingDTO> getActivityRankingByDeletedFalse();

    ActivityRanking findByDraftIdAndDeletedFalse(BigInteger draftId);

    ActivityRanking findTopByExpertiseIdAndDeletedFalseAndPublishedTrueOrderByStartDateDesc(Long expertiseId);

    List<ActivityRanking> getActivityRankingSettingsByUnitIdAndDeletedFalse(Long unitId);

    List<ActivityRanking> getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(Long unitId);

    //List<ActivityRanking> findAllByExpertiseIdInAndDeletedFalseAndEndDateGreaterThanEquals(List<Long> expertiseIds, LocalDate startDate);

}
