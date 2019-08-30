package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.staff.StaffTeamDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface TeamGraphRepository extends Neo4jBaseRepository<Team,Long>{

    @Query("MATCH (org:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true}) WHERE id(org)={0} with DISTINCT team \n" +
            "OPTIONAL MATCH (team)-[rel:"+TEAM_HAS_MEMBER+"]->(teamMembers:Staff) WHERE EXISTS(rel.leaderType) \n" +
            "WITH team,COLLECT(teamMembers) as teamMembers,COLLECT(rel) as rel,\n" +
            "CASE when rel.leaderType='MAIN_LEAD' THEN COLLECT(id(teamMembers)) ELSE NULL END AS mainTeamLeaderIds,\n" +
            "CASE when rel.leaderType='ACTING_LEAD' THEN COLLECT(id(teamMembers)) ELSE NULL END AS actingTeamLeaderIds\n" +
            "WITH DISTINCT team,COLLECT(DISTINCT mainTeamLeaderIds) as mainTeamLeaderIds,COLLECT(DISTINCT actingTeamLeaderIds) as actingTeamLeaderIds\n" +
            "RETURN COLLECT(DISTINCT{id:id(team),name:team.name,description:team.description,\n" +
            "mainTeamLeaderIds:CASE WHEN mainTeamLeaderIds[0] IS NULL THEN [] ELSE  mainTeamLeaderIds[0] END, \n" +
            "actingTeamLeaderIds:CASE WHEN actingTeamLeaderIds[0] IS NULL THEN [] ELSE  actingTeamLeaderIds[0] END}) AS teams")
    List<Map<String,Object>> getTeams(long unitId);

    @Query("MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "OPTIONAL MATCH (team)-[staffRel:"+TEAM_HAS_MEMBER+"]->(teamMembers:Staff) \n" +
            "WITH team,COLLECT(DISTINCT{staffId:id(teamMembers),teamType:staffRel.teamType}) as staffDetails\n" +
            "OPTIONAL MATCH (team)-[:"+TEAM_HAS_SKILLS+"]->(skills:Skill) with team, COLLECT (id(skills)) as skillIds ,staffDetails\n" +
            "RETURN id(team) as id, team.name as name, team.description as description, team.activityIds as activityIds, skillIds as skillIds,staffDetails as staffDetails")
    TeamDTO getTeamDetailsById(long teamId);

    @Query(" MATCH (t:Team),(s:Skill) WHERE id(s) IN {1} AND id(t)={0}  " +
            " CREATE UNIQUE (t)-[:"+TEAM_HAS_SKILLS+"]->(s) RETURN s")
    List<Skill> saveSkill(Long teamId, Set<Long> skill);

    @Query("MATCH (team:Team)-[skillTeamRel:"+TEAM_HAS_SKILLS+"]->(skill:Skill) WHERE id(team)={0} DETACH DELETE skillTeamRel")
    void removeAllSkillsFromTeam(Long teamId);

    @Query(" MATCH (t:Team),(os:OrganizationService) WHERE id(t) IN {0} AND id(os)={1}  " +
            " CREATE (t)-[:"+TEAM_HAS_SERVICES+"]->(os) RETURN os")
    List<OrganizationService> addSelectedSevices(Long teamId, Long[] organizationService);


    @Query("MATCH (t:Team)-[:"+TEAM_HAS_SKILLS+"]->(s:Skill)  with s AS skill " +
            "MATCH (sc:SkillCategory)-[:HAS_CATEGORY]-(skill) RETURN  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:COLLECT({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            "}) " +
            "} AS skillList ")
    List<Map<String,Object>> getSelectedSkills(Long teamId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff)={1}\n" +
            "MATCH (team)-[r:"+TEAM_HAS_MEMBER+"]->(staff) RETURN COUNT(r) as r")
    int countRelBetweenStaffAndTeam(long teamId, long staffId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff)={1}\n" +
            "Create (team)-[r:"+TEAM_HAS_MEMBER+"{isEnabled:true,lastModificationDate:{3},creationDate:{2}\n" +
            "}]->(staff) RETURN COUNT(r) as r")
    int linkOfTeamAndStaff(long teamId, long staffId, long creationDate, long lastModificationDate);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff)={1}\n" +
            "MATCH (team)-[r:"+TEAM_HAS_MEMBER+"\n" +
            "]->(staff) SET r.lastModificationDate={2},r.isEnabled={3} RETURN COUNT(r) as r")
    int updateStaffTeamRelationship(long teamId, long staffId, long lastModificationDate, boolean isEnabled);

    @Query("MATCH (team:Team{isEnabled:true})-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(team)={0} AND EXISTS(staffTeamRel.leaderType) DETACH DELETE staffTeamRel")
    void removeAllStaffsFromTeam(long teamId);

    @Query("MATCH (team:Team{isEnabled:true})-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(team)={1} AND id(staff) IN {0} DETACH DELETE staffTeamRel RETURN COUNT(staffTeamRel)>0")
    boolean removeStaffsFromTeam(List<Long> staffIds, Long teamId);

    @Query("MATCH (team:Team{isEnabled:true})-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(staff)={0} DETACH DELETE staffTeamRel")
    void removeStaffFromAllTeams(long staffId);

    @Query("MATCH (organization:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true,deleted:false})-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(staff)={0} AND id(organization)={1} RETURN \n" +
            "id(team) as teamId,team.name as name,staffTeamRel.teamType as teamType,staffTeamRel.leaderType as leaderType")
    List<StaffTeamDTO> getTeamDetailsOfStaff(Long staffId,Long unitId);

    @Query("MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "MATCH (team)<-[:"+HAS_TEAMS+"]-(organization:Unit) with organization,team\n" +
            "MATCH (position:Position)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitEmployment)-[:"+ APPLICABLE_IN_UNIT +"]->(organization) with position,team\n" +
            "MATCH (staff:Staff) WHERE (team)-[:"+TEAM_HAS_MEMBER+"]->(staff) OR (position)-[:"+BELONGS_TO+"]->(staff) with staff,team\n" +
            "OPTIONAL MATCH (team)-[r:"+TEAM_HAS_MEMBER+"]->(staff) with r,staff\n" +
            "RETURN DISTINCT {id:id(staff),firstName:staff.firstName+\" \" +staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,isSelected:CASE when r is null THEN false else r.isEnabled end,profilePic: {1} + staff.profilePic} as data order by data.firstName")
    List<Map<String,Object>> getAllStaffByOrganization(long teamId, String imageUrl);

    @Query("MATCH (org:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true,deleted:false}) WHERE id(org)={0} RETURN {id:id(team),name:team.name} as data order by data.name")
    List<Map<String,Object>> getAllTeamsInOrganization(long organizationId);

    @Query("MATCH (org:Unit)-[:"+HAS_TEAMS+"]->(team:Team{isEnabled:true,deleted:false}) WHERE id(org)={0} RETURN id(team) as id,team.name as name")
    List<TeamDTO> findAllTeamsInOrganization(long organizationId);

    @Query("MATCH (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) WHERE id(team)={0} AND id(os)={1} SET r.customName={2} \n"+
            "RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfSubServiceForTeam(Long teamId, Long organizationServiceId, String customName);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} WITH organizationService\n" +
            "MATCH (team:Team) WHERE id(team)={1} WITH team, organizationService\n" +
            "CREATE UNIQUE (team)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(organizationService) SET r.customName=organizationService.name RETURN true")
    Boolean addCustomNameOfServiceForTeam(Long subServiceId, Long teamId);

    @Query("MATCH (organization:Unit)-[:"+ HAS_TEAMS +"]->(team:Team) WHERE id(organization)={0} RETURN team")
    List<Team> getTeamsByOrganization(long organizationId);

    @Query("MATCH(organization:Unit)-[:" + HAS_TEAMS + "]->(team:Team {isEnabled:true}) WHERE id(organization)={0} AND id(team)<>{1} AND team.name =~{2}  " +
            " WITH COUNT(team) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean teamExistInOrganizationByName(Long organizationId, Long teamId, String teamName);

    @Query("MATCH(staff:Staff)-[:TEAM_HAS_MEMBER]-(team:Team) WHERE id(staff)={0} \n" +
            "WITH team.activityIds AS activityIds\n" +
            "UNWIND activityIds AS activities\n" +
            "RETURN DISTINCT activities")
    List<BigInteger> getTeamActivitiesOfStaff(Long staffId);

    @Query("MATCH(team:Team{deleted:false}) WHERE ANY(activity IN team.activityIds WHERE activity=toString({0})) " +
            "RETURN count(team)>0")
    boolean activityExistInTeamByActivityId(BigInteger activityId);
}
