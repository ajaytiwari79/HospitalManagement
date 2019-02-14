package com.kairos.persistence.repository.user.staff;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.StaffRelationship;
import com.kairos.persistence.model.staff.*;
import com.kairos.persistence.model.staff.employment.MainEmploymentQueryResult;
import com.kairos.persistence.model.staff.employment.StaffEmploymentDTO;
import com.kairos.persistence.model.staff.permission.UnitStaffQueryResult;
import com.kairos.persistence.model.staff.personal_details.OrganizationStaffWrapper;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffAdditionalInfoQueryResult;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.persistence.model.user.expertise.Response.ExpertiseLocationStaffQueryResult;
import com.kairos.persistence.model.user.filter.FavoriteFilterQueryResult;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.unit_position.query_result.StaffUnitPositionDetails;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@Repository
public interface StaffGraphRepository extends Neo4jBaseRepository<Staff, Long>, CustomStaffGraphRepository {

    @Override
    List<Staff> findAll();

    Staff findByExternalId(Long externalId);

    @Query("MATCH (team:Team)-[:" + TEAM_HAS_MEMBER + "{isEnabled:true}]->(staff:Staff) WHERE id(team)={0} " +
            " MATCH (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "RETURN id(staff) AS id,staff.firstName AS firstName,staff.lastName AS lastName,staff.familyName AS familyName,user.cprNumber AS cprNumber,staff.visitourId AS visitourId,{1} + staff.profilePic AS profilePic order by data.firstName")
    List<StaffPersonalDetailDTO> getStaffByTeamId(long teamId, String imageUrl);

    @Query("MATCH (group:Group)-[:" + HAS_TEAM + "]->(team:Team)-[:" + TEAM_HAS_MEMBER + "]->(staff:Staff) WHERE id(group)={0} " +
            " MATCH (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "RETURN id(staff) AS id,staff.firstName AS firstName,staff.lastName AS lastName,staff.familyName AS familyName,user.cprNumber AS cprNumber,staff.visitourId AS visitourId,{1} + staff.profilePic AS profilePic order by data.firstName")
    List<StaffPersonalDetailDTO> getStaffByGroupId(long groupId, String imageUrl);

    @Query(" MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) WHERE id(organization)={0} with employment\n" +
            "MATCH (organization:Organization)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) WHERE id(organization)={1} with staff\n " +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "OPTIONAL MATCH (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "RETURN {id:id(staff),name:staff.firstName+\" \" +staff.lastName,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:round ((timestamp()-user.dateOfBirth) / (365*24*60*60*1000)), gender:user.gender, profilePic:{2} + staff.profilePic, engineerType:id(engineerType)} AS data order by data.firstName\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) WHERE id(organization)={0} with employment\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) WHERE id(unit)={1} with unitPermission, staff\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, unitPermission, staff\n" +
            "OPTIONAL MATCH (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "RETURN {id:id(staff), name:staff.firstName+\" \" +staff.lastName, firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:round ((timestamp()-user.dateOfBirth) / (365*24*60*60*1000)), gender:user.gender, profilePic: {2} + staff.profilePic, engineerType:id(engineerType)} AS data order by data.firstName")
    List<Map<String, Object>> getStaffWithBasicInfo(long organizationId, long unitId, String imageUrl);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) WHERE id(organization)={0} with unitPermission\n" +
            "MATCH (staffs:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) with staffs " +
            "OPTIONAL MATCH (staffs)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) \n" +
            "with staffs,COLLECT(id(skills)) AS skills OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staffs) \n" +
            "with staffs,skills,COLLECT(id(teams)) AS teams \n" +
            "RETURN id(staffs) AS id,staffs.firstName+\" \" +staffs.lastName AS name,staffs.profilePic AS profilePic,teams,skills order by name")
    List<StaffAdditionalInfoQueryResult> getStaffAndCitizenDetailsOfUnit(long unitId);


    @Query("MATCH (unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(organization:Organization)<-[:"+IN_UNIT+"]-(unitPosition:UnitPosition) WHERE  id(unitPosition)={0} with unitPermission,organization,unitPosition \n" +
            "MATCH (unitPosition)<-[:"+BELONGS_TO_STAFF+"]-(staff:Staff)<-[:"+BELONGS_TO+"]-(employment:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission)  with staff,organization " +
            "MATCH (staff)-[:"+BELONGS_TO+"]->(user:User) with staff,organization,user " +
            "OPTIONAL MATCH (staff)-[:"+STAFF_HAS_SKILLS+"{isEnabled:true}]->(skills:Skill{isEnabled:true}) with staff,COLLECT(id(skills)) AS skills,organization,user" +
            " OPTIONAL MATCH (teams:Team)-[:"+TEAM_HAS_MEMBER+"{isEnabled:true}]->(staff) WITH staff,skills,COLLECT(id(teams)) AS teams,organization,user" +
            " RETURN id(staff) AS id,staff.firstName+\" \"+staff.lastName AS name,staff.profilePic AS profilePic,teams,skills,id(organization) AS unitId,id(user) AS staffUserId,user.cprNumber AS cprNumber order by name")
    StaffAdditionalInfoQueryResult getStaffInfoByUnitIdAndUnitPositionId(long unitPositionId);


    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission,organization " +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) where id(staff)={1} with staff,organization " +
            "Match (staff)-[:BELONGS_TO]->(user:User) with staff,organization,user " +
            "OPTIONAL MATCH (staff)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) with staff,collect(id(skills)) as skills,organization,user" +
            " OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff) with staff,skills,collect(id(teams)) as teams,organization,user" +
            " return id(staff) as id,staff.firstName+\" \"+staff.lastName as name,staff.profilePic as profilePic,teams,skills,id(organization) as unitId,id(user) as staffUserId,user.cprNumber as cprNumber order by name")
    StaffAdditionalInfoQueryResult getStaffInfoByUnitIdAndStaffId(long unitId, long staffId);

    @Query("MATCH (staff:Staff) WHERE id(staff) IN {1}  " +
            "MATCH (unitPermission:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]->(organization:Organization) WHERE id(organization)={0} \n" +
            "MATCH (staff)<-[:"+BELONGS_TO+"]-(employment:Employment)-[:"+HAS_UNIT_PERMISSIONS+"]->(unitPermission)   \n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]->(unitPosition:UnitPosition)-[:" + IN_UNIT + "]->(organization) \n " +
            "MATCH(unitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine) WHERE  NOT EXISTS(positionLine.endDate) OR date(positionLine.endDate) >= date() with staff,organization " +
            "OPTIONAL MATCH (staff)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) WITH staff,collect(id(skills)) as skills,organization\n" +
            "OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff) with staff,skills,collect(id(teams)) as teams,organization\n" +
            "RETURN id(staff) as id,staff.firstName+\" \"+staff.lastName as name,staff.profilePic as profilePic,teams,skills,id(organization) as unitId order by name")
    List<StaffAdditionalInfoQueryResult> getStaffInfoByUnitIdAndStaffIds(long unitId, List<Long> staffIds);

    @Query("MATCH (staff:Staff) WHERE id(staff)={0} MATCH (team)-[r:TEAM_HAS_MEMBER]->(staff) SET r.isEnabled=false RETURN r")
    List<StaffRelationship> removeStaffFromAllTeams(long staffId);

    @Query("MATCH (team:Team),(staff:Staff) WHERE id(team) IN {1} AND id(staff)={0} CREATE UNIQUE (team)-[:TEAM_HAS_MEMBER]-(staff) RETURN team")
    Staff editStaffWorkPlaces(long staffId, List<Long> staffIds);

    @Query("MATCH (unit:Organization),(staff:Staff) WHERE id(staff)={0} AND id(unit)={1} with staff,unit\n" +
            "MATCH (unit)-[orgSkillRelation:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with skill,staff, orgSkillRelation,unit\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=unit.showCountryTags with DISTINCT  skill,staff, orgSkillRelation,unit,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END AS ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + ORGANIZATION_HAS_TAG + "]-(unit) with  skill,staff, orgSkillRelation,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END AS otags\n" +
            "OPTIONAL MATCH (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill{isEnabled:true}) with skill,r,orgSkillRelation, ctags,otags\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with skill,skillCategory,r,orgSkillRelation,ctags,otags\n" +
            "RETURN {children:COLLECT({id:id(skill),name:case WHEN orgSkillRelation is null or orgSkillRelation.customName is null then skill.name else orgSkillRelation.customName end ,isSelected:r.isEnabled, tags:ctags+otags}),id:id(skillCategory),name:skillCategory.name} AS data")
    List<Map<String, Object>> getSkills(long staffId, long unitId);

    @Query("MATCH (staff:Staff),(skill:Skill) WHERE id(staff)={0} AND id(skill) IN {1} MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) set r.isEnabled=false,r.lastModificationDate={2} RETURN r")
    void deleteSkillFromStaff(long staffId, List<Long> skillId, long lastModificationDate);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) WHERE id(o)= {0}  WITH s AS staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) WHERE r.role = 'PLANNER' RETURN staff")
    List<Staff> findAllPlanners(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) WHERE id(o)= {0}  WITH s AS staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) WHERE r.role = 'VISITATOR' RETURN staff")
    List<Staff> findAllVisitator(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) WHERE id(o)= {0}  WITH s AS staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) WHERE r.role = 'MANAGER' RETURN staff")
    List<Staff> findAllManager(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) WHERE id(o)= {0}  WITH s AS staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) WHERE r.role = 'TEAM_LEADER' RETURN staff")
    List<Staff> findAllTeamLeader(Long organizationId);

    @Query("MATCH (s:Staff) WHERE s.organizationId={0} RETURN s")
    List<Staff> getUploadedStaffByOrganizationId(Long organizationId);

    @Query("MATCH (organization:Organization),(staff:Staff) WHERE id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "MATCH (employment:Employment)-[:BELONGS_TO]->(staff) with employment,organization\n" +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission)-[:APPLICABLE_IN_UNIT]->(organization) with unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_PARTIAL_LEAVES]->(partialLeave:PartialLeave)\n" +
            "RETURN partialLeave")
    List<PartialLeave> getPartialLeaves(long childOrganizationId, long staffId);

    @Query("MATCH (u:User)-[:BELONGS_TO]-(s:Staff) WHERE id(u)={0} with s " +
            "MATCH (t:Team)-[:TEAM_HAS_MEMBER] -(s) with t " +
            "MATCH (g:Group)-[:HAS_TEAM]-(t) with g " +
            "MATCH (o:Organization)-[:HAS_GROUP]-(g) RETURN o")
    Organization getStaffOrganization(Long id);

    @Query("MATCH(u:User)-[:BELONGS_TO]-(s:Staff)-[:HAS_CONTACT_DETAIL]->(c:ContactDetail) WHERE c.privatePhone={0} OR c.mobilePhone={0} RETURN u")
    List<User> getStaffByMobileNumber(String number);

    @Query("MATCH (s:Staff)-[:BELONGS_TO]->(u:User) WHERE id(u)={0} RETURN s")
    Staff getByUser(Long userId);

    @Query("MATCH (organization:Organization),(unit:Organization) WHERE id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "MATCH (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) with employment,unit\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff{email:{2}}) with employment\n" +
            "MATCH (empoyment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with unitPermission with count(unitPermission) AS count RETURN count")
    int countOfUnitEmployment(long organizationId, long unitId, String email);

    @Query("MATCH (organization:Organization),(unit:Organization) WHERE id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "MATCH (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "\n" +
            "optional MATCH (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "RETURN {id:id(staff),accessGroupId:id(accessGroup),email:staff.email,firstName:staff.firstName,lastName:staff.lastName,contactDetail:{landLinePhone:contactDetail.landLinePhone,mobilePhone:contactDetail.mobilePhone}} AS data")
    List<Map<String, Object>> getUnitManagers(long organizationId, long unitId);


    @Query("MATCH (s:Staff) WHERE id(s)={0} DELETE s")
    void deleteStaffById(long staffId);

    @Query("MATCH (s:Staff)-[rel]-(e:Employment) WHERE id(s)={0} AND id(e)={1} DELETE rel")
    void deleteStaffEmployment(long staffId, long employmentId);

    @Query("MATCH (s:Staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) WHERE id(s)={0} RETURN contactDetail")
    ContactDetail getContactDetail(long staffId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (organization)-[:HAS_GROUP]->(g:Group)-[:HAS_TEAM]->(team:Team) \n" +
            "MATCH (team)-[:TEAM_HAS_MEMBER]->(staff:Staff) WHERE id(staff) IN {1} \n" +
            " MATCH (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with COLLECT({id:id(skill),name:skill.name}) AS skills,staff,team\n" +
            "RETURN {id:id(team),name:team.name,staffList:COLLECT({id:id(staff), name:staff.firstName+\" \" +staff.lastName,cprNumber:user.cprNumber, profilePic: staff.profilePic, skills:case WHEN skills[0].id is null then [] else skills end})} AS data")
    List<Map<String, Object>> getTeamStaffList(Long organizationId, List staffIds);

    @Query("MATCH (staff:Staff) WHERE id(staff) IN {0} with staff\n" +
            " MATCH (staff)-[:"+BELONGS_TO+"]->(user:User) with staff,user" +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with COLLECT({id:id(skill),name:skill.name}) AS skills,staff,user\n" +
            "RETURN {skills:case WHEN skills[0].id is null then [] else skills end,id:id(staff),name:staff.firstName+\" \" +staff.lastName,cprNumber:user.cprNumber, profilePic: staff.profilePic} AS data")
    List<Map<String, Object>> getSkillsOfStaffs(List<Long> staffIds);

    @Query("MATCH (c:Client) , (s:Staff) WHERE id(c)={0}  AND id(s) IN {1}  " +
            "OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1  " +
            "  Create Unique (c)-[r:SERVED_BY_STAFF{type:'PREFERRED'}]->(s) RETURN count(r)")
    int createClientStaffPreferredRelation(Long clientId, List<Long> staffIds);


    @Query("MATCH (c:Client) , (s:Staff) WHERE id(c)={0}  AND id(s) IN {1}  OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1  Create Unique (c)-[r:SERVED_BY_STAFF{type:'FORBIDDEN'}]->(s) RETURN count(r)")
    int createClientStaffForbidRelation(Long clientId, List<Long> staffIds);

    @Query("MATCH (c:Client) , (s:Staff) WHERE id(c)={0}  AND id(s) IN {1}  MATCH  (c)-[r:SERVED_BY_STAFF]->(s)  delete r RETURN count(r)")
    int deleteClientStaffRelation(Long clientId, List<Long> staffIds);

    @Query("MATCH (staff:Staff),(skill:Skill) WHERE id (staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "Merge (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "ON CREATE SET r.creationDate={2},r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5}\n" +
            "ON MATCH SET r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5} RETURN true")
    void addSkillInStaff(long staffId, List<Long> skillId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel, boolean isEnabled);

    @Query("MATCH (staff:Staff),(skill:Skill) WHERE id(staff)={0} and id(skill) IN {1}\n" +
            "MATCH (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory) with skill, staff, skillCategory, r\n" +
            "MATCH (organization:Organization)-[orgHasSkill:" + ORGANISATION_HAS_SKILL + "]->(skill:Skill) WHERE id(organization)={2} with skill, staff, skillCategory,orgHasSkill, r \n" +
            "RETURN {id:id(r),skillId:id(skill),name:orgHasSkill.customName,skillCategory:skillCategory.name,startDate:r.startDate,endDate:r.endDate,visitourId:skill.visitourId,lastSyncInVisitour:r.lastModificationDate,status:r.isEnabled,level:r.skillLevel} AS data")
    List<Map<String, Object>> getStaffSkillInfo(long staffId, List<Long> skillId, long unitId);

    @Query("MATCH (staff:Staff) WHERE id(staff)={0} with staff\n" +
            "MATCH (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) WHERE id(expertise) IN {1} with staff,skill\n" +
            "MERGE (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "ON CREATE SET r.creationDate ={2},r.lastModificationDate ={3},r.isEnabled=true,r.skillLevel={4}\n" +
            "ON MATCH SET r.lastModificationDate = {3},r.skillLevel={4},r.isEnabled=true")
    void updateSkillsByExpertise(long staffId, List<Long> expertiseId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel);

    @Query("MATCH (staff:Staff) WHERE id(staff)={0} with staff\n" +
            "MATCH (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) WHERE id(expertise) IN {1} with staff,skill\n" +
            "MATCH (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "set r.isEnabled=false")
    void removeSkillsByExpertise(long staffId, List<Long> expertiseIds);

    Staff findByKmdExternalId(Long kmdExternalId);


    @Query("MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) WHERE id(organization)={0} AND id(staff)={1}\n" +
            " RETURN staff")
    Staff getStaffByUnitId(long unitId, long staffId);


    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) WHERE id(organization)={0} AND id(staff)={1}\n" +
            " RETURN staff, employment.startDateMillis AS employmentStartDate")
    StaffEmploymentDTO getStaffAndEmploymentByUnitId(long unitId, long staffId);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) WHERE id(organization)={0} with unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{name:\"COUNTRY_ADMIN\"}) with unitPermission\n" +
            "MATCH (employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff\n" +
            "RETURN id(staff)")
    List<Long> getCountryAdminIds(long organizationId);

    @Query("MATCH (organization:Organization),(unit:Organization) WHERE id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "MATCH (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitPermission\n" +
            "MATCH (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "optional MATCH (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "RETURN id(staff)")
    List<Long> getUnitManagersIds(long organizationId, long unitId);


    @Query("MATCH(user:User)  WHERE id(user)={1} \n" +
            "MATCH(staff:Staff)-[:BELONGS_TO]->(user) \n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "RETURN  id(staff) AS id,user.gender AS gender, user.pregnant AS pregnant,staff.profilePic AS profilePic, contactAddress.city AS city,contactAddress.province AS province , staff.firstName AS firstName,staff.lastName AS lastName,staff.employedSince AS employedSince,staff.badgeNumber AS badgeNumber, staff.userName AS userName,staff.externalId AS externalId,staff.organizationId AS organizationId,user.cprNumber AS cprNumber,staff.visitourTeamId AS visitourTeamId,staff.familyName AS familyName")
    List<StaffPersonalDetailDTO> getStaffInfoById(long unitId, long staffId);

    @Query("MATCH(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "OPTIONAL MATCH (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType,contactAddress, staff, user\n" +
            "RETURN distinct id(staff) AS id, contactAddress.city AS city,contactAddress.province AS province ,staff.firstName AS firstName,staff.lastName AS lastName,staff.employedSince AS employedSince,staff.badgeNumber AS badgeNumber, staff.userName AS userName,staff.externalId AS externalId,user.cprNumber AS cprNumber,staff.visitourTeamId AS visitourTeamId,staff.familyName AS familyName, user.gender AS gender, {1} + staff.profilePic AS profilePic, id(engineerType) AS engineerType")
    List<StaffPersonalDetailDTO> getAllStaffHavingUnitPositionByUnitId(long unitId, String imageUrl);


    @Query("MATCH(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) WHERE id(organization)={0}\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user,count(unitPos) as unitPosition ORDER BY staff.firstName ASC\n" +
            "return distinct {id:id(staff),name:staff.firstName+\" \"+staff.lastName,city:contactAddress.city, case  when unitPosition > 0 then TRUE else false end as unitPosition ,province:contactAddress.province  ,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:duration.between(date(u.dateOfBirth),date()).years, gender:user.gender, profilePic:{1} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName")
    List<Map<String, Object>> getAllStaffHavingUnitPositionByUnitIdMap(long unitId, String imageUrl);

    @Query("MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) WHERE id(organization)={0} \n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH(staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization)" +
            "OPTIONAL MATCH (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user,unitPos \n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)  with engineerType, staff,contactAddress, user ,count(unitPos) AS unitPosition ORDER BY staff.firstName ASC\n" +
            "RETURN  distinct id(staff) AS id,case  WHEN unitPosition > 0 then TRUE else false end AS unitPosition , contactAddress.city AS city,contactAddress.province AS province ,staff.firstName AS firstName,staff.lastName AS lastName,staff.employedSince AS employedSince,staff.badgeNumber AS badgeNumber, staff.userName AS userName,staff.externalId AS externalId,user.cprNumber AS cprNumber,staff.visitourTeamId AS visitourTeamId,staff.familyName AS familyName, user.gender AS gender, {1} + staff.profilePic AS profilePic, id(engineerType) AS engineerType")
    List<StaffPersonalDetailDTO> getAllStaffByUnitId(Long unitId, String imageUrl);

    @Query("MATCH (staff:Staff)-[:ENGINEER_TYPE]->(engineerType:EngineerType) WHERE id(staff)={0} RETURN id(engineerType)")
    Long getEngineerTypeId(Long staffId);


    @Query("MATCH (staff:Staff)-[:LANGUAGE]->(language:Language) WHERE id(staff)={0} RETURN id(language)")
    Long getLanguageId(Long staffId);

    @Query("MATCH (team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) WHERE id(staff)= {1} AND id(team)={0}  RETURN staff ")
    Staff getTeamStaff(Long teamId, Long staffId);

    @Query("MATCH (unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) WHERE id(unit)={1} with unitPermission\n" +
            "MATCH (unitPermission)<-[:" + HAS_UNIT_PERMISSIONS + "]-(employment:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{externalId:{0}}) RETURN count(staff)>0")
    Boolean staffAlreadyInUnit(Long externalId, Long unitId);

    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{externalId:{1}}) \n" +
            "WHERE id(organization)={0} with staff\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) with staff,contactAddress\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_DETAIL + "]->(contactDetail:ContactDetail) with staff,contactDetail,contactAddress\n" +
            "RETURN staff,id(contactAddress) AS contactAddressId,id(contactDetail) AS contactDetailId LIMIT 1")
    StaffQueryResult getStaffByExternalIdInOrganization(Long organizationId, Long externalId);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) WHERE id(organization)={0} with unitPermission ,organization\n" +

            "MATCH (o:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE id(o)=71 with et\n" +
            "OPTIONAL MATCH (o)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) with \n" +
            "COLLECT(CASE WHEN r IS NULL AND  et.allowedForContactPerson =true THEN  {id:id(et),allowedForContactPerson:et.allowedForContactPerson} \n" +
            "ELSE {id:id(et),allowedForContactPerson:r.allowedForContactPerson} END) AS employmentTypeSettings with filter\n" +
            "(x IN employmentTypeSettings WHERE x.allowedForContactPerson=true) AS filteredEmploymentType with extract(n IN filteredEmploymentType| n.id) AS extractedEmploymentTypeId \n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission)\n" +
            "MATCH (unitPermission)-[:HAS_UNIT_EMPLOYMENT_POSITION]->(p:Position)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType) WHERE id(et) IN extractedEmploymentTypeId\n" +
            "RETURN distinct id(staff) AS id, staff.firstName AS firstName,staff.lastName AS lastName")
    List<StaffPersonalDetailDTO> getAllMainEmploymentStaffDetailByUnitId(long unitId);

    @Query("MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilters:StaffFavouriteFilter{deleted:false}) WHERE id(staff)={0} with staffFavouriteFilters \n" +
            "MATCH (staffFavouriteFilters)-[:HAS_FILTER_GROUP]->(filterGroup:FilterGroup)-[:APPLICABLE_FOR]-(accessPage:AccessPage) WHERE accessPage.moduleId={1} \n" +
            "MATCH (staffFavouriteFilters)-[:FILTER_DETAIL]-(filterDetail:FilterSelection) with staffFavouriteFilters, COLLECT({id:id(filterDetail), name:filterDetail.name, value:filterDetail.value}) AS filterDetails\n" +
            "RETURN id(staffFavouriteFilters) AS id, staffFavouriteFilters.name AS name, filterDetails AS filtersData")
    List<FavoriteFilterQueryResult> getStaffFavouriteFiltersByStaffAndView(Long staffId, String moduleId);

    @Query("MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilters:StaffFavouriteFilter) WHERE id(staff)={0} AND id(staffFavouriteFilters)={1} RETURN staffFavouriteFilters")
    StaffFavouriteFilter getStaffFavouriteFiltersById(Long staffId, Long staffFavouriteFiltersId);


    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) WHERE id(organization)={1}" +
            "MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) WHERE id(user)={0} RETURN staff")
    Staff getStaffByUserId(Long userId, Long parentOrganizationId);

    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) WHERE id(organization)={1}" +
            "MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) WHERE user.cprNumber={0} RETURN count(staff)>0")
    Boolean isStaffExistsByCPRNumber(String cprNumber, Long parentOrganizationId);
// TODO CRITICAL ISSUE we are fetching all staff across all organisation i think it should be refactored
    @Query("MATCH (staff:Staff)-[:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(expertise) IN {1} RETURN staff")
    List<Staff> getStaffByExperties(Long unitId, List<Long> expertiesIds);

    @Query("MATCH (staff:Staff) WHERE id(staff) IN {0} with staff\n" +
            " OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with COLLECT(skill) AS skills RETURN skills")
    List<Skill> getSkillByStaffIds(List<Long> staffIds);

    @Query("MATCH(s:Staff) WHERE s.externalId IN {0} RETURN s.externalId")
    List<Long> findStaffByExternalIdIn(Set<Long> externalIdsOfStaff);

    @Query("MATCH(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPosition:UnitPosition{deleted:false}) WHERE staff.externalId={0} AND unitPosition.timeCareExternalId={1} " +
            "RETURN unitPosition AS unitPosition ,staff AS staff ")
    OrganizationStaffWrapper getStaff(Long externalId, Long timeCareExternalId);

    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)" +
            "WHERE id(organization)={1}" +
            "MATCH (staff)-[:" + BELONGS_TO + "]->(user:User) WHERE id(user)={0} " +
            "MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilter:StaffFavouriteFilter{deleted:false}) " +
            "WHERE id(staffFavouriteFilter) = {2} RETURN staffFavouriteFilter")
    StaffFavouriteFilter getStaffFavouriteFiltersOfStaffInOrganizationById(Long userId, Long organizationId, Long staffFavouriteFilterId);

    @Query("MATCH (staffFavouriteFilter:StaffFavouriteFilter)-[r:" + FILTER_DETAIL + "]->(filterDetail:FilterSelection) WHERE id(staffFavouriteFilter)={0} \n" +
            "DELETE r,filterDetail")
    void detachStaffFavouriteFilterDetails(Long staffFavouriteFilterId);

    @Query("MATCH(organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(employments:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{deleted:false}) WHERE id(organization)={0} " +
            "MATCH(staff)-[:" + HAS_CONTACT_DETAIL + "]->(contactDetail:ContactDetail) RETURN  id(staff) AS id, staff.firstName AS firstName, " +
            " staff.lastName AS lastName, contactDetail.privatePhone AS privatePhone ")
    List<StaffPersonalDetailDTO> getAllStaffWithMobileNumber(long unitId);


    @Query("MATCH(o:Organization)-[:"+HAS_EMPLOYMENTS+"]->"+"(e:Employment)-[:"+BELONGS_TO+"]->(s:Staff) WHERE id(o)={0} RETURN s")
    List<Staff> getAllStaffByUnitId(long unitId);


    @Query("MATCH(staff:Staff)-[: "+BELONGS_TO_STAFF+" ]-(unitPosition:UnitPosition{deleted:false})-[:"+IN_UNIT+"]-(organization:Organization) where id(organization) IN {0} " +
            "RETURN id(staff) as id,staff.firstName  as firstName,staff.lastName as lastName, collect(DISTINCT id(organization)) as unitIds")
    List<StaffKpiFilterQueryResult> getAllStaffIdAndNameByUnitId(List<Long> unitIds);

    @Query("MATCH (org:Organization) WITH org\n" +
            "MATCH (org)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO]->(user:User) WITH staff, user\n" +
            "MATCH (staff)-[:BELONGS_TO_STAFF]-(unitPosition:UnitPosition{deleted:false})-[:IN_UNIT]-(o:Organization)\n" +
            "with  COLLECT({id: id(staff),firstName:staff.firstName,lastName:staff.lastName, gender :user.gender, pregnant:user.pregnant, dateOfBirth:user.dateOfBirth}) AS staffData,o " +
            "RETURN  id(o) AS unitId, staffData AS staffList")
    List<UnitStaffQueryResult> getStaffListOfUnitWithBasicInfo();

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) WHERE id(user)={0} AND id(organization)={1}  RETURN staff")
    Staff findByUserId(Long userId, Long unitId);


    @Query("MATCH (staff:Staff)-[:"+BELONGS_TO+"]-(user:User)where id(staff)={0} with staff,user\n" +
            "MATCH (user)-[:"+BELONGS_TO+"]-(staffs:Staff)where id(staffs)<>{0} with staffs,user\n " +
            "MATCH (staffs)-[:"+BELONGS_TO+"]-(employment:Employment) with staffs,user,employment\n" +
            "WHERE (employment.mainEmploymentEndDate IS NULL OR employment.mainEmploymentEndDate>={1}) AND ((employment.mainEmploymentStartDate IS NOT NULL) AND({2} IS NULL OR employment.mainEmploymentStartDate<={2})) with user,employment,staffs\n" +
            "MATCH (employment)-[:"+HAS_EMPLOYMENTS+"]-(organization:Organization) RETURN employment,organization.name as organizationName")
    List<MainEmploymentQueryResult> getAllMainEmploymentByStaffId(Long staffId, Long mainEmploymentStartDate, Long mainEmploymentEndDate );

    @Query("MATCH(staff:Staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition:UnitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) WHERE id(expertise)={1} AND id(staff) IN {2}\n" +
            "MATCH(unitPosition)-[:"+IN_UNIT+"]-(organization:Organization) WHERE id(organization)={0}   \n" +
            "AND unitPosition.startDateMillis<={3} AND  (unitPosition.endDateMillis IS NULL or unitPosition.endDateMillis>={3})  \n" +
            "RETURN id(unitPosition) AS id , id(staff) AS staffId")
    List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffIds,Long currentMillis);

    @Query("MATCH(staff:Staff) WHERE id(staff) in {0} RETURN staff.email")
    List<String> getEmailsOfStaffByStaffIds(List<Long> staffIds);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) WHERE id(user)={0} AND id(organization)={1}  RETURN id(staff)")
    Long findStaffIdByUserId(Long userId, Long unitId);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization{isKairosHub:true})-[:"+HAS_SUB_ORGANIZATION+"]-(org:Organization) WHERE id(user)={0} AND id(org)={1}  RETURN id(staff)")
    Long findHubStaffIdByUserId(Long userId, Long unitId);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) WHERE id(user)={0} with staff\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition{deleted:false,published:true}) with staff,up \n" +
            "MATCH(up)-[: " + IN_UNIT + " ]-(org:Organization{deleted:false}) with staff,up,org\n" +
            "MATCH (up)-[: " + HAS_EXPERTISE_IN + "]-(exp:Expertise) with staff,up,org,exp \n"+
            "Optional MATCH (org)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) WHERE reasonCode.reasonCodeType={1} with staff,up,org,exp,reasonCode \n " +
            "RETURN id(staff) AS staffId,id(org) AS unitId,org.name AS unitName,org.timeZone AS timeZone,COLLECT(DISTINCT reasonCode) AS reasonCodes,COLLECT(DISTINCT {id:id(up),expertiseName:exp.name}) AS unitPosition")
    List<StaffInformationQueryResult> getStaffAndUnitTimezoneByUserIdAndReasonCode(Long id, ReasonCodeType reasonCodeType);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) WHERE id(user)={0} with staff\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition{deleted:false,published:true}) with staff,up \n" +
            "MATCH(up)-[: " + IN_UNIT + " ]-(org:Organization{deleted:false}) RETURN id(staff) AS staffId,id(org) AS unitId ")
    List<StaffInformationQueryResult> getStaffIdsAndUnitByUserId(Long userId);

    @Query("MATCH(staff:Staff)-[rel:"+STAFF_HAS_EXPERTISE+"]-(exp:Expertise) WHERE id(staff)={0} and id(exp)={1} set rel.expertiseStartDate = {2}  RETURN staff,exp")
    void updateStaffExpertiseRelation(Long staffId,Long expertiseId,Long millis);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) WHERE id(user)={0} with staff\n" +
            "MATCH(staff)-[:" + BELONGS_TO + "]-(employment:Employment)-[:" + HAS_EMPLOYMENTS + "]-(org:Organization{deleted:false}) with staff,org\n"+
            "RETURN id(staff) AS staffId,id(org) AS unitId,org.name AS unitName")
    List<StaffInformationQueryResult> getAllStaffsAndUnitDetailsByUserId(Long userId);

    @Query("Optional MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(e:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE staff.email=~{0} AND id(organization)={1} RETURN staff")
    Staff findStaffByEmailInOrganization(String email,Long unitId);

    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(e:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)-[:" + HAS_CONTACT_DETAIL + "]->(contactDetail:ContactDetail) WHERE id(organization)={1} " +
            "AND staff.email=~{0} OR contactDetail.privateEmail=~{0}  RETURN staff")
    Staff findStaffByEmailIdInOrganization(String email, Long unitId);

    @Query("MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE id(organization)={0} \n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) " +
            "MATCH (staff)-[:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(expertise) ={2} \n" +
            "RETURN  distinct id(staff) AS id,staff.firstName AS firstName,staff.lastName AS lastName,staff.userName AS userName,user.cprNumber AS cprNumber,user.gender AS gender, {1} + staff.profilePic AS profilePic")
    List<StaffPersonalDetailDTO> getAllStaffByUnitIdAndExpertiseId(Long unitId, String imageUrl,Long expertiseId);

    @Query("MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE id(organization)={0} \n" +
            "MATCH (staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(expertise) ={1} AND rel.unionRepresentative\n" +
            "SET rel.unionRepresentative=false " +
            "RETURN count(rel)")
    int removePreviousUnionRepresentativeOfExpertiseInUnit(Long unitId, Long expertiseId);

    @Query("MATCH (staff:Staff),(expertise:Expertise) WHERE id(staff)={0} AND id(expertise) ={1} \n" +
            "MATCH (staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise)\n" +
            "SET rel.unionRepresentative=true " +
            "RETURN count(rel) ")
    int assignStaffAsUnionRepresentativeOfExpertise(Long staffId, Long expertiseId);

    @Query("MATCH (organization:Organization),(expertise:Expertise) WHERE id(organization)={1} AND id(expertise) IN {0} " +
            "MATCH (organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "{unionRepresentative:true}]->(expertise) \n" +
            " MATCH(staff)-[:"+BELONGS_TO+"]-(user:User)\n" +
            "RETURN {id:id(staff),name:user.firstName} AS staff,id(expertise) AS expertiseId")
    List<ExpertiseLocationStaffQueryResult> findAllUnionRepresentativeOfExpertiseInUnit(List<Long> expertiseIds, Long unitId);

    @Query("MATCH (organization:Organization{deleted:false,isEnable:true})<-[:"+HAS_SUB_ORGANIZATION+"*]-(organizationHub:Organization{deleted:false,isEnable:true}) \n" +
            "-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]->(user:User)\n " +
            "WHERE id(organization)={0} AND organizationHub.isKairosHub=true AND id(user)={1} \n " +
            "RETURN staff")
    Staff getStaffByOrganizationHub(Long currentUnitId,Long userId);


    //TODO not delete

//    @Query("MATCH (org:Organization) WHERE id(org) IN {0}"+
//            "MATCH (org)-[:"+IN_UNIT+"]-(up:UnitPosition)-[:"+BELONGS_TO_STAFF+"]-(staff:Staff)"+
//            "MATCH (up)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine)"+
//            "WHERE  date(positionLine.startDate) <= date({2}) AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date({1}))"+
//            "CASE WHEN {3} is null THEN staff ELSE WHERE id(staff) in {3} END AS staff"+
//           " RETURN DISTINCT id(staff) AS id, staff.firstName AS firstName,staff.lastName AS lastName")
//    List<StaffPersonalDetailDTO> getStaffsByUnitIds(List<Long> unitIds,String startDate,String endDate,List<Long> staffIds);

//
//    @Query("MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE id(organization)={0}\n" +
//            "MATCH(staff)-[:"+BELONGS_TO_STAFF+"]-(up:UnitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine)"+
//            "WHERE  date(positionLine.startDate) <= date({2}) AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date({1}))"+
//            " RETURN DISTINCT id(staff) AS id, staff.firstName AS firstName,staff.lastName AS lastName")
//    List<StaffPersonalDetailDTO> getStaffsByUnitId(Long unitId,String startDate,String endDate);
//
//    @Query("MATCH (org:Organization),(emptype:EmploymentType) WHERE id(emptype) IN {1} AND id(org) = {0}"+
//            "MATCH (org)-[:"+HAS_EMPLOYMENTS+"]-(emp:Employment)-[:"+BELONGS_TO+"]-(staff:Staff)"+
//            "MATCH(staff)-[:"+BELONGS_TO_STAFF+"]-(up:UnitPosition)-[:"+HAS_POSITION_LINES+"]-(positionLine:UnitPositionLine)"+
//            "WHERE  date(positionLine.startDate) <= date({3}) AND (NOT exists(positionLine.endDate) OR date(positionLine.endDate) >= date({2}))"+
//            "MATCH (positionLine)-[:"+HAS_EMPLOYMENT_TYPE+"]-(emptype) RETURN DISTINCT id(staff) AS id, staff.firstName AS firstName,staff.lastName AS lastName")
//    List<StaffPersonalDetailDTO> getStaffsByUnitIdAndEmploymentType(Long unitId, List<Long> employmentType,String startDate,String endDate);

}

