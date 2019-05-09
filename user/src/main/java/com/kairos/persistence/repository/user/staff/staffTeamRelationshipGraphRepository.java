package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.organization.StaffTeamRelationShipQueryResult;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_TEAMS;
import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_MEMBER;

@Repository
public interface staffTeamRelationshipGraphRepository extends Neo4jBaseRepository<StaffTeamRelationship,Long> {


    @Query("MATCH(t:Team{deleted:false})-[rel:"+TEAM_HAS_MEMBER+"]-(staff:Staff{deleted:false}) WHERE id(staff) = {0} AND id(t)={1} return id(rel) as id, rel.leaderType as leaderType," +
            "rel.teamType as teamType,id(staff) as staffId,id(t) as teamId")
    StaffTeamRelationShipQueryResult findByStaffIdAndTeamId(Long staffId, Long teamId);

    @Query("MATCH(t:Team{deleted:false})-[rel:"+TEAM_HAS_MEMBER+"]-(staff:Staff{deleted:false}) WHERE id(t) IN {0} AND id(t)={1} return id(rel) as id, rel.leaderType as leaderType," +
            "rel.teamType as teamType,id(staff) as staffId,id(t) as teamId")
    List<StaffTeamRelationShipQueryResult> findAllByStaffIdAndTeamIds(Set<Long> teamIds, Long staffId);

    @Query("MATCH (org:Organization)-[:"+HAS_TEAMS+"]->(team:Team {isEnabled:true}) WHERE id(org)={0} with team\n" +
            "OPTIONAL MATCH (team)-[rel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE rel.leaderType=rel.leaderType='MAIN_LEAD'\n" +
            "with team,collect(staff) as teamMembers\n" +
            "UNWIND teamMembers  as teamMemberList WITH DISTINCT teamMemberList,team\n" +
            "RETURN distinct id(team) as id, team.name as name, team.description as description,collect(id(teamMemberList)) as lop")
    List<TeamDTO> findAllStaffTeamRelationShipIds(Long unitId);

    @Query("MATCH(t:Team{deleted:false})-[rel:"+TEAM_HAS_MEMBER+"]-(staff:Staff{deleted:false}) WHERE id(staff) = {0} AND id(t)<>{1} AND rel.teamType='MAIN'" +
            " RETURN COUNT(rel)>0")
    boolean anyMainTeamExists(Long staffId, Long teamId);

}
