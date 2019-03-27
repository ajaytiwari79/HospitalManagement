package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.organization.team.TeamDTO;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface TeamGraphRepository extends Neo4jBaseRepository<Team,Long>{

    @Query("MATCH (org:Organization)-[:"+ HAS_TEAMS +"]->(team:Team {isEnabled:true}) WHERE id(org)={0} with team\n" +
            "OPTIONAL MATCH (team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"{teamLeader:true}]->(teamLead:Staff) with team,teamLead\n" +
            "RETURN COLLECT({id:id(team), name:team.name, description:team.description, teamLeaderStaffId:id(teamLead)}) as teams")
    List<Map<String,Object>> getTeams(long unitId);

    @Query("MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "OPTIONAL MATCH (team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"{teamLeader:true}]->(teamLead:Staff) with team,teamLead\n" +
            "OPTIONAL MATCH (team)-[:"+TEAM_HAS_MEMBER+"]->(teamMembers:Staff) with team, teamLead,  COLLECT (id(teamMembers)) as teamMemberIds  \n"+
            "OPTIONAL MATCH (team)-[:"+TEAM_HAS_SKILLS+"]->(skills:Skill) with team, teamLead, teamMemberIds, COLLECT (id(skills)) as skillIds \n"+
            "RETURN id(team) as id, team.name as name, team.description as description, team.activityIds as activityIds, teamMemberIds as teamMemberIds, skillIds as skillIds, id(teamLead) as teamLeaderStaffId")
    TeamDTO getTeamDetailsById(long teamId);

    @Query("MATCH (t:Team)-[:"+TEAM_HAS_MEMBER+"]->(u:Staff) WHERE id(t)={0} RETURN u")
    List<Staff> getStaffInTeam(Long teamID);

    @Query("MATCH (t:Team)-[s:"+TEAM_HAS_MEMBER+"]->(u:User) WHERE id(u)={0} RETURN s.type")
    String getStaffType(Long id);

    @Query(" MATCH (t:Team),(s:Skill) WHERE id(s) IN {1} AND id(t)={0}  " +
            " CREATE UNIQUE (t)-[:"+TEAM_HAS_SKILLS+"]->(s) RETURN s")
    List<Skill> saveSkill(Long teamId, List<Long> skill);

    @Query("MATCH (team:Team)-[skillTeamRel:"+TEAM_HAS_SKILLS+"]->(skill:Skill) WHERE id(team)={0} DETACH DELETE staffTeamRel")
    void removeAllSkillsFromTeam(Long teamId);

    @Query(" MATCH (t:Team),(os:OrganizationService) WHERE id(t) IN {0} AND id(os)={1}  " +
            " CREATE (t)-[:"+TEAM_HAS_SERVICES+"]->(os) RETURN os")
    List<OrganizationService> addSelectedSevices(Long teamId, Long[] organizationService);


    @Query("MATCH (t:Team)-[:"+TEAM_HAS_SKILLS+"]->(s:Skill)  with s AS skill " +
            "MATCH (sc:SkillCategory)-[:HAS_CATEGORY]-(skill) RETURN  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:collect({ " +
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


    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff)={1}\n" +
           "CREATE UNIQUE (team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"{isEnabled:true, teamLeader:true}]->(staff)")
    void assignTeamLeaderToTeam(long teamId, long staffId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff)={1} \n" +
            "OPTIONAL MATCH (team)-[existingStaffTeamRel:"+TEAM_HAS_MEMBER+"{teamLeader:true}]->(teamStaffs:Staff) WHERE id(teamStaffs) <> {1} DETACH DELETE existingStaffTeamRel \n"+
            "CREATE UNIQUE (team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"{isEnabled:true, teamLeader:true}]->(staff)")
    void updateTeamLeaderOfTeam(long teamId, long staffId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team)={0} AND id(staff) IN {1}  " +
            "CREATE UNIQUE (team)-[:"+TEAM_HAS_MEMBER+"]->(staff)")
    void updateStaffsInTeam(long teamId, List<Long> staffIds);

    @Query("MATCH (team:Team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(team)={0} DETACH DELETE staffTeamRel")
    void removeAllStaffsFromTeam(long teamId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(staff)={0} AND id(team) IN {1}  " +
            "CREATE UNIQUE (team)-[:"+TEAM_HAS_MEMBER+"]->(staff)")
    void assignStaffInTeams(long staffId, List<Long> teamIds);

    @Query("MATCH (team:Team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(staff)={0} DETACH DELETE staffTeamRel")
    void removeStaffFromAllTeams(long staffId);

    @Query("MATCH (team:Team)-[staffTeamRel:"+TEAM_HAS_MEMBER+"]->(staff:Staff) WHERE id(staff)={0} RETURN id(team)")
    List<Long> getTeamsOfStaff(long staffId);

    @Query("MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "MATCH (team)<-[:"+HAS_TEAMS+"]-(organization:Organization) with organization,team\n" +
            "MATCH (position:Position)-[:"+ HAS_UNIT_PERMISSIONS +"]->(unitEmployment)-[:"+ APPLICABLE_IN_UNIT +"]->(organization) with position,team\n" +
            "MATCH (staff:Staff) WHERE (team)-[:"+TEAM_HAS_MEMBER+"]->(staff) OR (position)-[:"+BELONGS_TO+"]->(staff) with staff,team\n" +
            "OPTIONAL MATCH (team)-[r:"+TEAM_HAS_MEMBER+"]->(staff) with r,staff\n" +
            "RETURN DISTINCT {id:id(staff),firstName:staff.firstName+\" \" +staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,isSelected:CASE when r is null then false else r.isEnabled end,profilePic: {1} + staff.profilePic} as data order by data.firstName")
    List<Map<String,Object>> getAllStaffByOrganization(long teamId, String imageUrl);

    @Query("MATCH (org:Organization)-[:"+HAS_TEAMS+"]->(team:Team) WHERE id(org)={0} RETURN {id:id(team),name:team.name} as data order by data.name")
    List<Map<String,Object>> getAllTeamsInOrganization(long organizationId);

    @Query("MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "MATCH (team)-[:"+TEAM_HAS_LOCATION+"]->(contactAddress:ContactAddress) with contactAddress\n" +
            "OPTIONAL MATCH (contactAddress)-[:"+ZIP_CODE+"]->(zipCode:ZipCode) with zipCode,contactAddress\n" +
            "OPTIONAL MATCH (contactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality) with zipCode,contactAddress,municipality\n" +
            " RETURN {id:id(contactAddress),houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,street1:contactAddress.street1,zipCodeId:id(zipCode),city:contactAddress.city,municipalityName:contactAddress.municipalityName,regionName:contactAddress.regionName,province:contactAddress.province,country:contactAddress.country,latitude:contactAddress.latitude,longitude:contactAddress.longitude,streetUrl:contactAddress.streetUrl,municipalityId:id(municipality)} as data")
    Map<String,Object> getContactAddressOfTeam(long teamId);

      @Query("MATCH (team:Team) WHERE id(team)={0}\n" +
            "MATCH (team)<-[:"+HAS_TEAMS+"]-(organization:Organization) with organization\n" +
            "MATCH (organization)-[r:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) with os,r\n" +
            "MATCH (oranizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) with {children: CASE when os is NULL then [] else collect({id:id(os),name:os.name,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(oranizationService),name:oranizationService.name,description:oranizationService.description} as availableServices\n" +
            "RETURN {availableServices:collect(availableServices)} as data\n" +
            "UNION\n" +
            "MATCH (team:Team) WHERE id(team)={0} with team\n" +
            "MATCH (team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService{isEnabled:true}) with r,os,team \n" +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) with organizationService,r,os,team\n" +
            "OPTIONAL MATCH (team)-[teamServiceCustomNameRelation:"+HAS_CUSTOM_SERVICE_NAME_FOR+"]-(organizationService:OrganizationService)\n" +
            "with {children: CASE when os is NULL then [] else collect({id:id(os),name:os.name, customName:CASE WHEN r IS null THEN os.name ELSE r.customName END, description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),customName:CASE WHEN teamServiceCustomNameRelation IS NULL THEN organizationService.name ELSE teamServiceCustomNameRelation.customName END,name:organizationService.name,description:organizationService.description} as selectedServices\n" +
            "RETURN {selectedServices:collect(selectedServices)} as data")
    List<Map<String,Object>> getOrganizationServicesOfTeam(long teamId);

    @Query("MATCH (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) WHERE id(team)={0} AND id(os)={1} RETURN COUNT(r) as countOfRel")
    int countOfServices(long teamId, long organizationServiceId);

    @Query("MATCH (team:Team),(organizationService:OrganizationService) WHERE id(team)={0} AND id(organizationService) IN {1} create unique (team)-[r:"+TEAM_HAS_SERVICES+"{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:organizationService.name}]->(organizationService) ")
    void addServiceInTeam(long teamId, long organizationServiceId, long creationDate, long lastModificationDate);

    @Query("MATCH (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) WHERE id(team)={0} AND id(os)={1} SET r.isEnabled={2},r.lastModificationDate={3},r.customName=os.name ")
    void updateOrganizationService(long teamId, long organizationServiceId, boolean isEnabled, long lastModificationDate);

    @Query("MATCH (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) WHERE id(team)={0} AND id(os)={1} SET r.customName={2} \n"+
            "RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfSubServiceForTeam(Long teamId, Long organizationServiceId, String customName);

    @Query("MATCH (team:Team),(os:OrganizationService) WHERE id(team)={1} AND id(os)={0} WITH team,os\n" +
            "MERGE (team)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os) \n" +
            "ON CREATE SET r.customName={2}\n" +
            "ON MATCH SET r.customName={2}\n" +
            " RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfServiceForTeam(Long serviceId, Long teamId, String customName);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} WITH organizationService\n" +
            "MATCH (team:Team) WHERE id(team)={1} WITH team, organizationService\n" +
            "CREATE UNIQUE (team)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(organizationService) SET r.customName=organizationService.name RETURN true")
    Boolean addCustomNameOfServiceForTeam(Long subServiceId, Long teamId);

      @Query("MATCH (team:Team) WHERE id(team)={0}\n" +
            "MATCH (team)<-[:"+ HAS_TEAMS +"]-(organization:Organization)\n" +
            "MATCH (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:"+ORG_TYPE_HAS_SKILL+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with DISTINCT skill,organization\n" +
            "MATCH (organization)-[r:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,r,organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:"+HAS_TAG+"]-(tag:Tag)<-["+COUNTRY_HAS_TAG+"]-(c:Country) WHERE tag.countryTag=organization.showCountryTags with  skill,r,organization,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags            \n" +
            "OPTIONAL MATCH (skill:Skill)-[:"+HAS_TAG+"]-(tag:Tag)<-["+ORGANIZATION_HAS_TAG+"]-(organization) with  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "MATCH (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags+otags})} as availableSkills\n" +
            "RETURN {availableSkills:collect(availableSkills)} as data\n" +
            "UNION\n" +
            "MATCH (team:Team) WHERE id(team)={0} \n" +
            "MATCH (team)<-[:"+ HAS_TEAMS +"]-(organization:Organization) \n" +
            "MATCH (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:"+ORG_TYPE_HAS_SKILL+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with DISTINCT skill,organization,team\n" +
            "MATCH (organization)-[:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,team,organization\n" +
            "MATCH (team)-[r:"+TEAM_HAS_SKILLS+"{isEnabled:true}]->(skill) with skill,r,organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:"+HAS_TAG+"]-(tag:Tag)<-["+COUNTRY_HAS_TAG+"]-(c:Country) WHERE tag.countryTag=organization.showCountryTags with  skill,organization,r,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:"+HAS_TAG+"]-(tag:Tag)<-["+ORGANIZATION_HAS_TAG+"]-(organization) with  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "MATCH (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true,tags:ctags+otags})} as selectedSkills\n" +
            "RETURN {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String,Object>> getSkillsOfTeam(long teamId);

    @Query("MATCH (team:Team),(skill:Skill) WHERE id (team)={0} AND id(skill)={1} with team,skill\n" +
            "Merge (team)-[r:"+TEAM_HAS_SKILLS+"]->(skill)\n" +
            "ON CREATE SET r.visitourId={2},r.creationDate={3},r.lastModificationDate={4},r.isEnabled={5}\n" +
            "ON MATCH SET r.visitourId={2},r.lastModificationDate={3},r.isEnabled={5} ")
    void addSkillInTeam(long teamId, long skillId, String visitourId, long creationDate, long lastModificationDate, boolean isEnabled);

    @Query("MATCH (organization:Organization)-[:"+ HAS_TEAMS +"]->(team:Team) WHERE id(organization)={0} RETURN team")
    List<Team> getTeamsByOrganization(long organizationId);

    @Query("MATCH (team:Team) WHERE id(team)={0}\n" +
            "MATCH (team)<-[:"+ HAS_TEAMS +"]-(organization:Organization) \n" +
            "MATCH (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:"+ORG_TYPE_HAS_SKILL+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with DISTINCT skill,organization,team\n" +
            "MATCH (organization)-[:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,team\n" +
            "MATCH (team)-[r:"+TEAM_HAS_SKILLS+"{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill)-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with skillCategory,skill,r\n" +
            "OPTIONAL MATCH (staff:Staff)-[staffSkillRel:"+STAFF_HAS_SKILLS+"{isEnabled:true}]->(skill) WHERE id(staff) IN {1} with {staff:CASE when staffSkillRel is null then [] else collect(id(staff)) end} as staff,skillCategory,skill,r\n" +
            "RETURN {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:skill.name,description:skill.description,isSelected:CASE when r is null then false else true end,isEdited:true,staff:staff.staff})} as data")
    List<Map<String, Object>> getAssignedSkillsOfStaffByTeam(long unitId, List<Long> staffId);

    @Query("MATCH (organization:Organization)-[:" + HAS_TEAMS + "]->(team:Team {isEnabled:true}) WHERE id(team)={0} RETURN id(organization)")
    Long getOrganizationIdByTeam(Long teamId);

    @Query("MATCH(organization:Organization)-[:" + HAS_TEAMS + "]->(team:Team {isEnabled:true}) WHERE id(organization)={0} AND id(team)<>{1} AND team.name =~{2}  " +
            " WITH COUNT(team) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean teamExistInOrganizationByName(Long organizationId, Long teamId, String teamName);
}
