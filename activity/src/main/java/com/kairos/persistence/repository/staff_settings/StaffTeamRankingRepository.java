package com.kairos.persistence.repository.staff_settings;

import com.kairos.persistence.model.staff_settings.StaffTeamRanking;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffTeamRankingRepository extends MongoBaseRepository<StaffTeamRanking, BigInteger> {

    List<StaffTeamRanking> getStaffTeamRankingByStaffIdAndDeletedFalse(Long staffId);

    @Query("{staffId:?0, deleted:false, published:true,'startDate':{$lte:?1}, '$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
    StaffTeamRanking getStaffTeamRanking(Long staffId, LocalDate endDate);
}
