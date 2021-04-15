package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.staff.StaffTeamRanking;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StaffTeamRankingGraphRepository extends Neo4jBaseRepository<StaffTeamRanking,Long> {

    List<StaffTeamRanking> findByStaffIdAndDeletedFalse(Long staffId);

    List<StaffTeamRanking> findByStaffIdAndPublishedTrueAndDeletedFalse(Long staffId);

    StaffTeamRanking findByDraftIdAndDeletedFalse(Long draftId);

//    @Query("{staffId:?0, deleted:false, published:true,'startDate':{$lte:?1}, '$or':[{'endDate':{$exists:false}},{'endDate':{$gte:?1}}]}")
//    StaffTeamRanking getStaffTeamRanking(Long staffId, LocalDate endDate);
}
