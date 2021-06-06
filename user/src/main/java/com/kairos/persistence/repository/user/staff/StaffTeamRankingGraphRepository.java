package com.kairos.persistence.repository.user.staff;

import com.kairos.enums.team.TeamType;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.staff.StaffTeamRanking;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;
import static com.kairos.persistence.model.constants.RelationshipConstants.IN_UNIT;


@Repository
public interface StaffTeamRankingGraphRepository extends Neo4jBaseRepository<StaffTeamRanking,Long> {

    List<StaffTeamRanking> findByUnitIdAndStaffIdAndDeletedFalse(Long unitId, Long staffId);

    List<StaffTeamRanking> findByUnitIdAndStaffIdAndPublishedTrueAndDeletedFalse(Long unitId, Long staffId);

    StaffTeamRanking findByDraftIdAndDeletedFalse(Long draftId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE id(teamRankingInfo) IN {0} DETACH DELETE teamRankingInfo")
    void removeTeamRankingInfo(Set<Long> removeTeamRankingInfoIds);

    @Query("MATCH (teamRankingInfo:TeamRankingInfo{deleted:false}) WHERE teamRankingInfo.teamId={0} " +
            "SET teamRankingInfo.activityId={1}")
    void updateActivityIdInTeamRanking(Long teamId, String activityId);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE  staffTeamRanking.unitId={0} AND staffTeamRanking.staffId = {1} AND teamRankingInfo.teamId={2} " +
            "SET teamRankingInfo.teamType={3}")
    void updateTeamType(Long unitId, Long staffId, Long teamId, TeamType newTeamType);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking{deleted:false})-[rel:TEAM_RANKING_INFO]->(teamRankingInfo:TeamRankingInfo) WHERE staffTeamRanking.unitId = {0} AND staffTeamRanking.staffId = {1} AND date(staffTeamRanking.startDate)<=DATE({2}) AND (staffTeamRanking.endDate IS NULL OR date(staffTeamRanking.endDate)>=DATE({2})) " +
            "RETURN staffTeamRanking,rel,teamRankingInfo")
    StaffTeamRanking getApplicableStaffTeamRanking(Long unitId, Long staffId, String date);

    @Query("MATCH (staffTeamRanking:StaffTeamRanking) WHERE id(staffTeamRanking) IN {0} AND staffTeamRanking.published=false " +
            "SET staffTeamRanking.deleted=true")
    void setDeleted(List<Long> deleteDraftIds);

    @Query("MATCH(staff:Staff)-[:" + BELONGS_TO_STAFF + "]->(employment:Employment)-[:" + IN_UNIT + "]->(organization:Unit) WHERE id(organization)={0}" +
            "RETURN DISTINCT id(staff) AS id")
    List<Long> getAllStaffIdsByUnitId(long unitId);

    @Query("MATCH (organization:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true,deleted:false})-[staffRel:"+TEAM_HAS_MEMBER+"]->(teamMembers:Staff) where id(organization)={0} AND id(teamMembers)={1} AND staffRel.teamMembership=true \n" +
            " RETURN team,staffRel,teamMembers")
    List<StaffTeamRelationship> getStaffTeamDetails(Long unitId, Long staffId);

    @Query("MATCH (organization:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true,deleted:false})-[staffRel:"+TEAM_HAS_MEMBER+"]->(teamMembers:Staff) where id(organization)={0} AND id(teamMembers)={1} AND staffRel.teamMembership=true AND staffRel.startDate IS NULL \n" +
            " SET staffRel.startDate={2}")
    void updateStaffTeamRelationStartDate(Long unitId, Long staffId,String startDate);
}
