package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.OrganizationService;
import com.kairos.persistence.model.organization.team.Team;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.staff.Staff;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 7/10/16.
 */
@Repository
public interface TeamGraphRepository extends GraphRepository<Team>{

    @Query("MATCH (group:Group)-[:HAS_TEAM]->(team:Team) where id(group)={0} with team\n" +
            "optional match (team)-[:TEAM_HAS_LOCATION]->(contactAddress:ContactAddress) with contactAddress,team\n" +
            "optional Match (contactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with zipCode,contactAddress,team\n" +
            "return collect({id:id(team),name:team.name,visitourId:team.visitourId,contactAddress:case when contactAddress is null then [] else {id:id(contactAddress),city:contactAddress.city,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street1:contactAddress.street1,zipCodeId:id(zipCode),floorNumber:contactAddress.floorNumber,houseNumber:contactAddress.houseNumber,province:contactAddress.province,country:contactAddress.country,regionName:contactAddress.regionName,\n" +
            "municipalityName:contactAddress.municipalityName} end}) as teams")
    List<Map<String,Object>> getTeams(long groupId);

    @Query("MATCH (t:Team)-[:"+TEAM_HAS_MEMBER+"]->(u:Staff) where id(t)={0} return u")
    List<Staff> getStaffInTeam(Long teamID);

    @Query("MATCH (t:Team)-[s:"+TEAM_HAS_MEMBER+"]->(u:User) where id(u)={0} return s.type")
    String getStaffType(Long id);

    @Query(" Match (t:Team),(s:Skill) where id(t) IN {0} AND id(s)={1}  " +
            " CREATE (t)-[:TEAM_HAS_SKILLS]->(s) return s")
    List<Skill> saveSkill(Long teamId, Long[] skill);

    @Query(" Match (t:Team),(os:OrganizationService) where id(t) IN {0} AND id(os)={1}  " +
            " CREATE (t)-[:TEAM_HAS_SERVICES]->(os) return os")
    List<OrganizationService> addSelectedSevices(Long teamId, Long[] organizationService);


    @Query("MATCH (t:Team)-[:"+TEAM_HAS_SKILLS+"]->(s:Skill)  with s AS skill " +
            "MATCH (sc:SkillCategory)-[:HAS_CATEGORY]-(skill) return  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:collect({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            "}) " +
            "} AS skillList ")
    List<Map<String,Object>> getSelectedSkills(Long teamId);

    @Query("Match (team:Team),(staff:Staff) where id(team)={0} AND id(staff)={1}\n" +
            "Match (team)-[r:TEAM_HAS_MEMBER]->(staff) return count(r) as r")
    int countRelBetweenStaffAndTeam(long teamId, long staffId);

    @Query("Match (team:Team),(staff:Staff) where id(team)={0} AND id(staff)={1}\n" +
            "Create (team)-[r:TEAM_HAS_MEMBER{isEnabled:true,lastModificationDate:{3},creationDate:{2}\n" +
            "}]->(staff) return count(r) as r")
    int linkOfTeamAndStaff(long teamId, long staffId, long creationDate, long lastModificationDate);

    @Query("Match (team:Team),(staff:Staff) where id(team)={0} AND id(staff)={1}\n" +
            "Match (team)-[r:TEAM_HAS_MEMBER\n" +
            "]->(staff) SET r.lastModificationDate={2},r.isEnabled={3} return count(r) as r")
    int updateStaffTeamRelationship(long teamId, long staffId, long lastModificationDate, boolean isEnabled);

    @Query("Match (team:Team) where id(team)={0} with team\n" +
            "MATCH (team)<-[:"+HAS_TEAM+"]-(group:Group)<-[:"+HAS_GROUP+"]-(organization:Organization) with organization,team\n" +
            "Match (employment:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment)-[:"+PROVIDED_BY+"]->(organization) with employment,team\n" +
            "Match (staff:Staff) where (team)-[:"+TEAM_HAS_MEMBER+"]->(staff) OR (employment)-[:"+BELONGS_TO+"]->(staff) with staff,team\n" +
            "optional Match (team)-[r:"+TEAM_HAS_MEMBER+"]->(staff) with r,staff\n" +
            "return distinct {id:id(staff),firstName:staff.firstName+\" \" +staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,isSelected:case when r is null then false else r.isEnabled end,profilePic: {1} + staff.profilePic} as data order by data.firstName")
    List<Map<String,Object>> getAllStaffByOrganization(long teamId, String imageUrl);

    @Query("Match (org:Organization)-[:HAS_GROUP]->(g:Group)-[:HAS_TEAM]->(team:Team) where id(org)={0} return {id:id(team),name:team.name,visitourId:team.visitourId} as data order by data.name")
    List<Map<String,Object>> getAllTeamsInOrganization(long organizationId);

    @Query("Match (team:Team) where id(team)={0} with team\n" +
            "Match (team)-[:"+TEAM_HAS_LOCATION+"]->(contactAddress:ContactAddress) with contactAddress\n" +
            "optional match (contactAddress)-[:"+ZIP_CODE+"]->(zipCode:ZipCode) with zipCode,contactAddress\n" +
            "optional match (contactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality) with zipCode,contactAddress,municipality\n" +
            " return {id:id(contactAddress),houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,street1:contactAddress.street1,zipCodeId:id(zipCode),city:contactAddress.city,municipalityName:contactAddress.municipalityName,regionName:contactAddress.regionName,province:contactAddress.province,country:contactAddress.country,latitude:contactAddress.latitude,longitude:contactAddress.longitude,streetUrl:contactAddress.streetUrl,municipalityId:id(municipality)} as data")
    Map<String,Object> getContactAddressOfTeam(long teamId);

    @Query("Match (team:Team) where id(team)={0}\n" +
            "Match (team)<-[:HAS_TEAM]-(group:Group)<-[:"+HAS_GROUP+"]-(organization:Organization) with organization\n" +
            "Match (organization)-[r:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) with os,r\n" +
            "match (oranizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(oranizationService),name:oranizationService.name,description:oranizationService.description} as availableServices\n" +
            "return {availableServices:collect(availableServices)} as data\n" +
            "UNION\n" +
            "Match (team:Team) where id(team)={0}\n" +
            "match (team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService{isEnabled:true}) with r,os \n" +
            "match (oranizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) with oranizationService,r,os\n" +
            "with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(oranizationService),name:oranizationService.name,description:oranizationService.description} as selectedServices\n" +
            "return {selectedServices:collect(selectedServices)} as data")
    List<Map<String,Object>> getOrganizationServicesOfTeam(long teamId);

    @Query("Match (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) where id(team)={0} AND id(os)={1} return count(r) as countOfRel")
    int countOfServices(long teamId, long organizationServiceId);

    @Query("Match (team:Team),(organizationService:OrganizationService) where id(team)={0} AND id(organizationService) IN {1} create unique (team)-[r:"+TEAM_HAS_SERVICES+"{creationDate:{2},lastModificationDate:{3},isEnabled:true}]->(organizationService) return r")
    void addServiceInTeam(long teamId, long organizationServiceId, long creationDate, long lastModificationDate);

    @Query("Match (team:Team)-[r:"+TEAM_HAS_SERVICES+"]->(os:OrganizationService) where id(team)={0} AND id(os)={1} SET r.isEnabled={2},r.lastModificationDate={3} return r")
    void updateOrganizationService(long teamId, long organizationServiceId, boolean isEnabled, long lastModificationDate);

    @Query("Match (team:Team) where id(team)={0}\n" +
            "Match (team)<-[:"+HAS_TEAM+"]-(group:Group)<-[:"+HAS_GROUP+"]-(organization:Organization) with organization\n" +
            "Match (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) with subType,organization\n" +
            "Match (subType)-[:"+ORG_TYPE_HAS_EXPERTISE+"{isEnabled:true}]->(expertise:Expertise)-[r:"+EXPERTISE_HAS_SKILLS+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization\n" +
            "Match (organization)-[r:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            "return {availableSkills:collect(availableSkills)} as data\n" +
            "UNION\n" +
            "Match (team:Team) where id(team)={0} with team\n" +
            "Match (team)<-[:"+HAS_TEAM+"]-(group:Group)<-[:"+HAS_GROUP+"]-(organization:Organization) with organization,team\n" +
            "Match (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) with subType,organization,team\n" +
            "Match (subType)-[:"+ORG_TYPE_HAS_EXPERTISE+"{isEnabled:true}]->(expertise:Expertise)-[r:EXPERTISE_HAS_SKILLS{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization,team\n" +
            "Match (organization)-[:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,team\n"+
            "match (team)-[r:"+TEAM_HAS_SKILLS+"{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true})} as selectedSkills\n" +
            "return {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String,Object>> getSkillsOfTeam(long teamId);

    @Query("Match (team:Team),(skill:Skill) where id (team)={0} AND id(skill)={1} with team,skill\n" +
            "Merge (team)-[r:"+TEAM_HAS_SKILLS+"]->(skill)\n" +
            "ON CREATE SET r.visitourId={2},r.creationDate={3},r.lastModificationDate={4},r.isEnabled={5}\n" +
            "ON MATCH SET r.visitourId={2},r.lastModificationDate={3},r.isEnabled={5} return true")
    void addSkillInTeam(long teamId, long skillId, String visitourId, long creationDate, long lastModificationDate, boolean isEnabled);

    @Query("Match (organization:Organization)-[:"+HAS_GROUP+"]->(g:Group)-[:"+HAS_TEAM+"]->(team:Team) where id(organization)={0} return team")
    List<Team> getTeamsByOrganization(long organizationId);

    @Query("Match (team:Team) where id(team)={0}\n" +
            "Match (team)<-[:"+HAS_TEAM+"]-(group:Group)<-[:"+HAS_GROUP+"]-(organization:Organization) with organization,team\n" +
            "Match (organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) with subType,organization,team\n" +
            "Match (subType)-[:"+ORG_TYPE_HAS_EXPERTISE+"{isEnabled:true}]->(expertise:Expertise)-[r:"+EXPERTISE_HAS_SKILLS+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization,team\n" +
            "Match (organization)-[:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) with skill,team\n" +
            "Match (team)-[r:"+TEAM_HAS_SKILLS+"{isEnabled:true}]->(skill) with skill,r\n" +
            "Match (skill)-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with skillCategory,skill,r\n" +
            "optional match (staff:Staff)-[staffSkillRel:"+STAFF_HAS_SKILLS+"{isEnabled:true}]->(skill) where id(staff) IN {1} with {staff:case when staffSkillRel is null then [] else collect(id(staff)) end} as staff,skillCategory,skill,r\n" +
            "return {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:skill.name,description:skill.description,isSelected:case when r is null then false else true end,isEdited:true,staff:staff.staff})} as data")
    List<Map<String, Object>> getAssignedSkillsOfStaffByTeam(long unitId, List<Long> staffId);

}
