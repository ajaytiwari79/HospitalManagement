package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.organization.StaffTeamRelationShipQueryResult;
import com.kairos.persistence.model.organization.StaffTeamRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.TEAM_HAS_MEMBER;

@Repository
public interface StaffTeamRelationshipGraphRepository extends Neo4jBaseRepository<StaffTeamRelationship,Long> {


    @Query("MATCH(t:Team{deleted:false})-[rel:"+TEAM_HAS_MEMBER+"]-(staff:Staff{deleted:false}) WHERE id(staff) = {0} AND id(t)={1} return id(rel) as id, rel.leaderType as leaderType," +
            "rel.teamType as teamType,id(staff) as staffId,id(t) as teamId")
    StaffTeamRelationShipQueryResult findByStaffIdAndTeamId(Long staffId, Long teamId);

    @Query("MATCH(t:Team{deleted:false})-[rel:"+TEAM_HAS_MEMBER+"]-(staff:Staff{deleted:false}) WHERE id(staff) = {0} AND id(t)<>{1} AND rel.teamType='MAIN'" +
            " RETURN COUNT(rel)>0")
    boolean anyMainTeamExists(Long staffId, Long teamId);

}
