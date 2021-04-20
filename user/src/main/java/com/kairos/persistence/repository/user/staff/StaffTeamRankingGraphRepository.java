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

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FILTERS;

@Repository
public interface StaffTeamRankingGraphRepository extends Neo4jBaseRepository<StaffTeamRanking,Long> {

    List<StaffTeamRanking> findByStaffIdAndDeletedFalse(Long staffId);

    List<StaffTeamRanking> findByStaffIdAndPublishedTrueAndDeletedFalse(Long staffId);

    StaffTeamRanking findByDraftIdAndDeletedFalse(Long draftId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking)-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE id(teamRankingInfo) IN {0} DETACH DELETE teamRankingInfo")
    void removeTeamRankingInfo(Set<Long> removeTeamRankingInfoIds);

    @Query("MATCH (teamRankingInfo:TeamRankingInfo) WHERE teamRankingInfo.teamId={0} " +
            "SET teamRankingInfo.activityId={1}")
    void updateActivityIdInTeamRanking(Long teamId, BigInteger activityId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking)-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE staffTeamRanking.staffId = {0} AND teamRankingInfo.teamId={1} " +
            "SET teamRankingInfo.teamType={2}")
    void updateTeamType(Long staffId, Long teamId, TeamType newTeamType);
}
