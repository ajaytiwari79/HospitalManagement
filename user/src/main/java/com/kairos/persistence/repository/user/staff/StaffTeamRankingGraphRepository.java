package com.kairos.persistence.repository.user.staff;

import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.staff.StaffTeamRanking;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Repository
public interface StaffTeamRankingGraphRepository extends Neo4jBaseRepository<StaffTeamRanking,Long> {

    List<StaffTeamRanking> findByStaffIdAndDeletedFalse(Long staffId);

    List<StaffTeamRanking> findByStaffIdAndPublishedTrueAndDeletedFalse(Long staffId);

    StaffTeamRanking findByDraftIdAndDeletedFalse(Long draftId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE id(teamRankingInfo) IN {0} DETACH DELETE teamRankingInfo")
    void removeTeamRankingInfo(Set<Long> removeTeamRankingInfoIds);

    @Query("MATCH (teamRankingInfo:TeamRankingInfo{deleted:false}) WHERE teamRankingInfo.teamId={0} " +
            "SET teamRankingInfo.activityId={1}")
    void updateActivityIdInTeamRanking(Long teamId, String activityId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE staffTeamRanking.staffId = {0} AND teamRankingInfo.teamId={1} " +
            "SET teamRankingInfo.teamType={2}")
    void updateTeamType(Long staffId, Long teamId, TeamType newTeamType);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE staffTeamRanking.staffId = {0} AND date(staffTeamRanking.startDate)<=DATE({1}) AND (staffTeamRanking.endDate IS NULL OR date(staffTeamRanking.endDate)>=DATE({1})) " +
            "RETURN staffTeamRanking,rel,teamRankingInfo")
    StaffTeamRanking getApplicableStaffTeamRanking(Long staffId, String date);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking) WHERE id(staffTeamRanking) IN {0} AND staffTeamRanking.published=false " +
            "SET staffTeamRanking.deleted=true")
    void setDeleted(List<Long> deleteDraftIds);
}
