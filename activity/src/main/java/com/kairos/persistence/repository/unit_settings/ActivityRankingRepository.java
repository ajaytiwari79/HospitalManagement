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

    ActivityRanking findByDraftIdAndDeletedFalse(BigInteger draftId);

    List<ActivityRanking> getActivityRankingSettingsByUnitIdAndDeletedFalse(Long unitId);

    List<ActivityRanking> getActivityRankingSettingsByCountryIdAndDeletedFalse(Long countryId);

    List<ActivityRanking> getActivityRankingSettingsByUnitIdAndPublishedTrueAndDeletedFalse(Long unitId);

    @Query("{unitId:?0, deleted:false, published:true,'startDate':{$lte:?1}, '$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
    ActivityRanking getCurrentlyActiveActivityRankingSettings(Long unitId, LocalDate shiftDate);

    @Query("{'deleted':false, 'published':false,'_id':{'$in':?0}}")
    List<ActivityRanking> getAllDraftByIds(List<BigInteger> deleteDraftCopy);

    @Query("{countryId:?0, deleted:false, published:true,expertiseId:{$exists:true}}")
    List<ActivityRanking> getCurrentlyActiveActivityRankingSettings(Long countryId);

}
