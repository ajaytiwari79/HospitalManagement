package com.kairos.persistence.repository.user.staff;

import com.kairos.enums.reason_code.ReasonCodeType;
import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.client.ContactDetail;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.StaffRelationship;
import com.kairos.persistence.model.staff.PartialLeave;
import com.kairos.persistence.model.staff.StaffFavouriteFilter;
import com.kairos.persistence.model.staff.StaffQueryResult;
import com.kairos.persistence.model.staff.StaffInformationQueryResult;
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

    @Query("MATCH (team:Team)-[:" + TEAM_HAS_MEMBER + "{isEnabled:true}]->(staff:Staff) where id(team)={0} " +
            " Match (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "return id(staff) as id,staff.firstName as firstName,staff.lastName as lastName,staff.familyName as familyName,user.cprNumber as cprNumber,staff.visitourId as visitourId,{1} + staff.profilePic as profilePic order by data.firstName")
    List<StaffPersonalDetailDTO> getStaffByTeamId(long teamId, String imageUrl);

    @Query("MATCH (group:Group)-[:" + HAS_TEAM + "]->(team:Team)-[:" + TEAM_HAS_MEMBER + "]->(staff:Staff) where id(group)={0} " +
            " Match (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "return id(staff) as id,staff.firstName as firstName,staff.lastName as lastName,staff.familyName as familyName,user.cprNumber as cprNumber,staff.visitourId as visitourId,{1} + staff.profilePic as profilePic order by data.firstName")
    List<StaffPersonalDetailDTO> getStaffByGroupId(long groupId, String imageUrl);

    @Query(" MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (organization:Organization)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(organization)={1} with staff\n " +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "return {id:id(staff),name:staff.firstName+\" \" +staff.lastName,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:round ((timestamp()-user.dateOfBirth) / (365*24*60*60*1000)), gender:user.gender, profilePic:{2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={1} with unitPermission, staff\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, unitPermission, staff\n" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "return {id:id(staff), name:staff.firstName+\" \" +staff.lastName, firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:round ((timestamp()-user.dateOfBirth) / (365*24*60*60*1000)), gender:user.gender, profilePic: {2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName")
    List<Map<String, Object>> getStaffWithBasicInfo(long organizationId, long unitId, String imageUrl);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission\n" +
            "MATCH (staffs:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) with staffs " +
            "OPTIONAL MATCH (staffs)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) \n" +
            "with staffs,collect(id(skills)) as skills OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staffs) \n" +
            "with staffs,skills,collect(id(teams)) as teams \n" +
            "return id(staffs) as id,staffs.firstName+\" \" +staffs.lastName as name,staffs.profilePic as profilePic,teams,skills order by name")
    List<StaffAdditionalInfoQueryResult> getStaffAndCitizenDetailsOfUnit(long unitId);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission,organization MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) where id(staff)={1} with staff,organization Match (staff)-[:BELONGS_TO]->(user:User) with staff,organization,user OPTIONAL MATCH (staff)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) with staff,collect(id(skills)) as skills,organization,user OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff) with staff,skills,collect(id(teams)) as teams,organization,user return id(staff) as id,staff.firstName+\" \"+staff.lastName as name,staff.profilePic as profilePic,teams,skills,id(organization) as unitId,id(user) as staffUserId,user.cprNumber as cprNumber order by name")
    StaffAdditionalInfoQueryResult getStaffInfoByUnitIdAndStaffId(long unitId, long staffId);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission,organization\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) where id(staff) IN {1} with staff,organization\n" +
            "OPTIONAL MATCH (staff)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) with staff,collect(id(skills)) as skills,organization\n" +
            "OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staff) with staff,skills,collect(id(teams)) as teams,organization\n" +
            "return id(staff) as id,staff.firstName+\" \"+staff.lastName as name,staff.profilePic as profilePic,teams,skills,id(organization) as unitId order by name")
    List<StaffAdditionalInfoQueryResult> getStaffInfoByUnitIdAndStaffIds(long unitId, List<Long> staffIds);

    @Query("MATCH (staff:Staff) where id(staff)={0} Match (team)-[r:TEAM_HAS_MEMBER]->(staff) SET r.isEnabled=false return r")
    List<StaffRelationship> removeStaffFromAllTeams(long staffId);

    @Query("Match (team:Team),(staff:Staff) where id(team) IN {1} AND id(staff)={0} CREATE UNIQUE (team)-[:TEAM_HAS_MEMBER]-(staff) return team")
    Staff editStaffWorkPlaces(long staffId, List<Long> staffIds);

    @Query("Match (unit:Organization),(staff:Staff) where id(staff)={0} AND id(unit)={1} with staff,unit\n" +
            "Match (unit)-[orgSkillRelation:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with skill,staff, orgSkillRelation,unit\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=unit.showCountryTags with DISTINCT  skill,staff, orgSkillRelation,unit,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + ORGANIZATION_HAS_TAG + "]-(unit) with  skill,staff, orgSkillRelation,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "OPTIONAL MATCH (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill{isEnabled:true}) with skill,r,orgSkillRelation, ctags,otags\n" +
            "Match (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with skill,skillCategory,r,orgSkillRelation,ctags,otags\n" +
            "return {children:collect({id:id(skill),name:case when orgSkillRelation is null or orgSkillRelation.customName is null then skill.name else orgSkillRelation.customName end ,isSelected:r.isEnabled, tags:ctags+otags}),id:id(skillCategory),name:skillCategory.name} as data")
    List<Map<String, Object>> getSkills(long staffId, long unitId);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} AND id(skill) IN {1} match (staff)-[r:STAFF_HAS_SKILLS]->(skill) set r.isEnabled=false,r.lastModificationDate={2} return r")
    void deleteSkillFromStaff(long staffId, List<Long> skillId, long lastModificationDate);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) where id(o)= {0}  WITH s as staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) where r.role = 'PLANNER' return staff")
    List<Staff> findAllPlanners(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) where id(o)= {0}  WITH s as staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) where r.role = 'VISITATOR' return staff")
    List<Staff> findAllVisitator(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) where id(o)= {0}  WITH s as staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) where r.role = 'MANAGER' return staff")
    List<Staff> findAllManager(Long organizationId);

    @Query("MATCH (o:Organization)-[*..5]->(s:Staff) where id(o)= {0}  WITH s as staff " +
            "MATCH (t:Team)-[r:TEAM_HAS_MEMBER]-(staff) where r.role = 'TEAM_LEADER' return staff")
    List<Staff> findAllTeamLeader(Long organizationId);

    @Query("MATCH (s:Staff) where s.organizationId={0} return s")
    List<Staff> getUploadedStaffByOrganizationId(Long organizationId);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (employment:Employment)-[:BELONGS_TO]->(staff) with employment,organization\n" +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission)-[:APPLICABLE_IN_UNIT]->(organization) with unitPermission\n" +
            "Match (unitPermission)-[:HAS_PARTIAL_LEAVES]->(partialLeave:PartialLeave)\n" +
            "return partialLeave")
    List<PartialLeave> getPartialLeaves(long childOrganizationId, long staffId);

    @Query("MATCH (u:User)-[:BELONGS_TO]-(s:Staff) where id(u)={0} with s " +
            "Match (t:Team)-[:TEAM_HAS_MEMBER] -(s) with t " +
            "MATCH (g:Group)-[:HAS_TEAM]-(t) with g " +
            "Match (o:Organization)-[:HAS_GROUP]-(g) return o")
    Organization getStaffOrganization(Long id);

    @Query("Match(u:User)-[:BELONGS_TO]-(s:Staff)-[:HAS_CONTACT_DETAIL]->(c:ContactDetail) where c.privatePhone={0} OR c.mobilePhone={0} return u")
    List<User> getStaffByMobileNumber(String number);

    @Query("MATCH (s:Staff)-[:BELONGS_TO]->(u:User) where id(u)={0} return s")
    Staff getByUser(Long userId);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) with employment,unit\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff{email:{2}}) with employment\n" +
            "Match (empoyment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with unitPermission with count(unitPermission) as count return count")
    int countOfUnitEmployment(long organizationId, long unitId, String email);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitPermission\n" +
            "Match (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "\n" +
            "optional Match (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "return {id:id(staff),accessGroupId:id(accessGroup),email:staff.email,firstName:staff.firstName,lastName:staff.lastName,contactDetail:{landLinePhone:contactDetail.landLinePhone,mobilePhone:contactDetail.mobilePhone}} as data")
    List<Map<String, Object>> getUnitManagers(long organizationId, long unitId);


    @Query("MATCH (s:Staff) where id(s)={0} DELETE s")
    void deleteStaffById(long staffId);

    @Query("MATCH (s:Staff)-[rel]-(e:Employment) where id(s)={0} AND id(e)={1} DELETE rel")
    void deleteStaffEmployment(long staffId, long employmentId);

    @Query("MATCH (s:Staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) where id(s)={0} return contactDetail")
    ContactDetail getContactDetail(long staffId);

    @Query("Match (organization:Organization) where id(organization)={0}\n" +
            "Match (organization)-[:HAS_GROUP]->(g:Group)-[:HAS_TEAM]->(team:Team) \n" +
            "Match (team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(staff) IN {1} \n" +
            " Match (staff)-[:"+BELONGS_TO+"]->(user:User) " +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with collect({id:id(skill),name:skill.name}) as skills,staff,team\n" +
            "return {id:id(team),name:team.name,staffList:collect({id:id(staff), name:staff.firstName+\" \" +staff.lastName,cprNumber:user.cprNumber, profilePic: staff.profilePic, skills:case when skills[0].id is null then [] else skills end})} as data")
    List<Map<String, Object>> getTeamStaffList(Long organizationId, List staffIds);

    @Query("Match (staff:Staff) where id(staff) IN {0} with staff\n" +
            " Match (staff)-[:"+BELONGS_TO+"]->(user:User) with staff,user" +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with collect({id:id(skill),name:skill.name}) as skills,staff,user\n" +
            "return {skills:case when skills[0].id is null then [] else skills end,id:id(staff),name:staff.firstName+\" \" +staff.lastName,cprNumber:user.cprNumber, profilePic: staff.profilePic} as data")
    List<Map<String, Object>> getSkillsOfStaffs(List<Long> staffIds);

    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  " +
            "OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1  " +
            "  Create Unique (c)-[r:SERVED_BY_STAFF{type:'PREFERRED'}]->(s) return count(r)")
    int createClientStaffPreferredRelation(Long clientId, List<Long> staffIds);


    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1  Create Unique (c)-[r:SERVED_BY_STAFF{type:'FORBIDDEN'}]->(s) return count(r)")
    int createClientStaffForbidRelation(Long clientId, List<Long> staffIds);

    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  MATCH  (c)-[r:SERVED_BY_STAFF]->(s)  delete r return count(r)")
    int deleteClientStaffRelation(Long clientId, List<Long> staffIds);

    @Query("Match (staff:Staff),(skill:Skill) where id (staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "Merge (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "ON CREATE SET r.creationDate={2},r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5}\n" +
            "ON MATCH SET r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5} return true")
    void addSkillInStaff(long staffId, List<Long> skillId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel, boolean isEnabled);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} and id(skill) IN {1}\n" +
            "Match (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory) with skill, staff, skillCategory, r\n" +
            "Match (organization:Organization)-[orgHasSkill:" + ORGANISATION_HAS_SKILL + "]->(skill:Skill) where id(organization)={2} with skill, staff, skillCategory,orgHasSkill, r \n" +
            "return {id:id(r),skillId:id(skill),name:orgHasSkill.customName,skillCategory:skillCategory.name,startDate:r.startDate,endDate:r.endDate,visitourId:skill.visitourId,lastSyncInVisitour:r.lastModificationDate,status:r.isEnabled,level:r.skillLevel} as data")
    List<Map<String, Object>> getStaffSkillInfo(long staffId, List<Long> skillId, long unitId);

    @Query("Match (staff:Staff) where id(staff)={0} with staff\n" +
            "Match (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) where id(expertise) IN {1} with staff,skill\n" +
            "MERGE (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "ON CREATE SET r.creationDate ={2},r.lastModificationDate ={3},r.isEnabled=true,r.skillLevel={4}\n" +
            "ON MATCH SET r.lastModificationDate = {3},r.skillLevel={4},r.isEnabled=true")
    void updateSkillsByExpertise(long staffId, List<Long> expertiseId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel);

    @Query("Match (staff:Staff) where id(staff)={0} with staff\n" +
            "Match (expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) where id(expertise) IN {1} with staff,skill\n" +
            "Match (staff)-[r:" + STAFF_HAS_SKILLS + "]->(skill)\n" +
            "set r.isEnabled=false")
    void removeSkillsByExpertise(long staffId, List<Long> expertiseIds);

    Staff findByKmdExternalId(Long kmdExternalId);


    @Query("MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) where id(organization)={0} AND id(staff)={1}\n" +
            " return staff")
    Staff getStaffByUnitId(long unitId, long staffId);


    @Query("MATCH (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(employment:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) where id(organization)={0} AND id(staff)={1}\n" +
            " return staff, employment.startDateMillis as employmentStartDate")
    StaffEmploymentDTO getStaffAndEmploymentByUnitId(long unitId, long staffId);

    @Query("Match (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission\n" +
            "Match (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{name:\"COUNTRY_ADMIN\"}) with unitPermission\n" +
            "Match (employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff\n" +
            "return id(staff)")
    List<Long> getCountryAdminIds(long organizationId);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission{isUnitManagerEmployment:true})-[:APPLICABLE_IN_UNIT]->(unit) with employment,unitPermission\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitPermission\n" +
            "Match (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "optional Match (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "return id(staff)")
    List<Long> getUnitManagersIds(long organizationId, long unitId);


    @Query("match(user:User)  where id(user)={1} \n" +
            "match(staff:Staff)-[:BELONGS_TO]->(user) \n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "return  id(staff) as id,user.gender as gender, user.pregnant as pregnant,staff.profilePic as profilePic, contactAddress.city as city,contactAddress.province as province , staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,staff.organizationId as organizationId,user.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName")
    List<StaffPersonalDetailDTO> getStaffInfoById(long unitId, long staffId);

    @Query("match(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) where id(organization)={0}\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType,contactAddress, staff, user\n" +
            "return distinct id(staff) as id, contactAddress.city as city,contactAddress.province as province ,staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,user.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName, user.gender as gender, {1} + staff.profilePic as profilePic, id(engineerType) as engineerType")
    List<StaffPersonalDetailDTO> getAllStaffHavingUnitPositionByUnitId(long unitId, String imageUrl);


    @Query("match(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization:Organization) where id(organization)={0}\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)\n" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user,count(unitPos) as unitPosition ORDER BY staff.firstName ASC\n" +
            "return distinct {id:id(staff),name:staff.firstName+\" \"+staff.lastName,city:contactAddress.city, case  when unitPosition > 0 then TRUE else false end as unitPosition ,province:contactAddress.province  ,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:user.cprNumber,visitourId:staff.visitourId, age:round ((timestamp()-user.dateOfBirth) / (365*24*60*60*1000)), gender:user.gender, profilePic:{1} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName")
    List<Map<String, Object>> getAllStaffHavingUnitPositionByUnitIdMap(long unitId, String imageUrl);

    @Query("match (organization:Organization)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff) where id(organization)={0} \n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "optional MATCH(staff)-[:BELONGS_TO_STAFF]-(unitPos:UnitPosition{deleted:false})-[:IN_UNIT]-(organization)" +
            "OPTIONAL Match (staff)-[:" + ENGINEER_TYPE + "]->(engineerType:EngineerType) with engineerType, staff, user,unitPos \n" +
            "optional MATCH (staff)-[:" + HAS_CONTACT_ADDRESS + "]-(contactAddress:ContactAddress)  with engineerType, staff,contactAddress, user ,count(unitPos) as unitPosition ORDER BY staff.firstName ASC\n" +
            "return  distinct id(staff) as id,case  when unitPosition > 0 then TRUE else false end as unitPosition , contactAddress.city as city,contactAddress.province as province ,staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,user.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName, user.gender as gender, {1} + staff.profilePic as profilePic, id(engineerType) as engineerType")
    List<StaffPersonalDetailDTO> getAllStaffByUnitId(Long unitId, String imageUrl);

    @Query("MATCH (staff:Staff)-[:ENGINEER_TYPE]->(engineerType:EngineerType) where id(staff)={0} return id(engineerType)")
    Long getEngineerTypeId(Long staffId);


    @Query("MATCH (staff:Staff)-[:LANGUAGE]->(language:Language) where id(staff)={0} return id(language)")
    Long getLanguageId(Long staffId);

    @Query("Match (team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(staff)= {1} AND id(team)={0}  return staff ")
    Staff getTeamStaff(Long teamId, Long staffId);

    @Query("Match (unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(unit:Organization) where id(unit)={1} with unitPermission\n" +
            "Match (unitPermission)<-[:" + HAS_UNIT_PERMISSIONS + "]-(employment:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{externalId:{0}}) return count(staff)>0")
    Boolean staffAlreadyInUnit(Long externalId, Long unitId);

    @Query("Match (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{externalId:{1}}) \n" +
            "where id(organization)={0} with staff\n" +
            "optional match (staff)-[:" + HAS_CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) with staff,contactAddress\n" +
            "optional match (staff)-[:" + HAS_CONTACT_DETAIL + "]->(contactDetail:ContactDetail) with staff,contactDetail,contactAddress\n" +
            "return staff,id(contactAddress) as contactAddressId,id(contactDetail) as contactDetailId LIMIT 1")
    StaffQueryResult getStaffByExternalIdInOrganization(Long organizationId, Long externalId);

    @Query("MATCH (unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(organization:Organization) where id(organization)={0} with unitPermission ,organization\n" +

            "MATCH (o:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE id(o)=71 with et\n" +
            "OPTIONAL MATCH (o)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) with \n" +
            "collect(CASE WHEN r IS NULL AND  et.allowedForContactPerson =true THEN  {id:id(et),allowedForContactPerson:et.allowedForContactPerson} \n" +
            "ELSE {id:id(et),allowedForContactPerson:r.allowedForContactPerson} END) as employmentTypeSettings with filter\n" +
            "(x IN employmentTypeSettings WHERE x.allowedForContactPerson=true) as filteredEmploymentType with extract(n IN filteredEmploymentType| n.id) AS extractedEmploymentTypeId \n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission)\n" +
            "match (unitPermission)-[:HAS_UNIT_EMPLOYMENT_POSITION]->(p:Position)-[:" + HAS_EMPLOYMENT_TYPE + "]->(et:EmploymentType) WHERE id(et) IN extractedEmploymentTypeId\n" +
            "return distinct id(staff) as id, staff.firstName as firstName,staff.lastName as lastName")
    List<StaffPersonalDetailDTO> getAllMainEmploymentStaffDetailByUnitId(long unitId);

    @Query("MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilters:StaffFavouriteFilter{deleted:false}) where id(staff)={0} with staffFavouriteFilters \n" +
            "MATCH (staffFavouriteFilters)-[:HAS_FILTER_GROUP]->(filterGroup:FilterGroup)-[:APPLICABLE_FOR]-(accessPage:AccessPage) where accessPage.moduleId={1} \n" +
            "MATCH (staffFavouriteFilters)-[:FILTER_DETAIL]-(filterDetail:FilterSelection) with staffFavouriteFilters, collect({id:id(filterDetail), name:filterDetail.name, value:filterDetail.value}) as filterDetails\n" +
            "return id(staffFavouriteFilters) as id, staffFavouriteFilters.name as name, filterDetails as filtersData")
    List<FavoriteFilterQueryResult> getStaffFavouriteFiltersByStaffAndView(Long staffId, String moduleId);

    @Query("MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilters:StaffFavouriteFilter) where id(staff)={0} AND id(staffFavouriteFilters)={1} return staffFavouriteFilters")
    StaffFavouriteFilter getStaffFavouriteFiltersById(Long staffId, Long staffFavouriteFiltersId);


    @Query("Match (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) where id(organization)={1}" +
            "Match (staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={0} return staff")
    Staff getStaffByUserId(Long userId, Long parentOrganizationId);

    @Query("Match (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff) where id(organization)={1}" +
            "Match (staff)-[:" + BELONGS_TO + "]->(user:User) where user.cprNumber={0} return count(staff)>0")
    Boolean isStaffExistsByCPRNumber(String cprNumber, Long parentOrganizationId);
// TODO CRITICAL ISSUE we are fetching all staff across all organisation i think it should be refactored
    @Query("MATCH (staff:Staff)-[:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) where id(expertise) IN {1} return staff")
    List<Staff> getStaffByExperties(Long unitId, List<Long> expertiesIds);

    @Query("Match (staff:Staff) where id(staff) IN {0} with staff\n" +
            " OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with collect(skill) as skills return skills")
    List<Skill> getSkillByStaffIds(List<Long> staffIds);

    @Query("match(s:Staff) where s.externalId IN {0} return s.externalId")
    List<Long> findStaffByExternalIdIn(Set<Long> externalIdsOfStaff);

    @Query("match(staff:Staff)-[:BELONGS_TO_STAFF]-(unitPosition:UnitPosition{deleted:false}) where staff.externalId={0} AND unitPosition.timeCareExternalId={1} " +
            "return unitPosition as unitPosition ,staff as staff ")
    OrganizationStaffWrapper getStaff(Long externalId, Long timeCareExternalId);

    @Query("Match (organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(emp:Employment)-[:" + BELONGS_TO + "]->(staff:Staff)" +
            "where id(organization)={1}" +
            "Match (staff)-[:" + BELONGS_TO + "]->(user:User) where id(user)={0} " +
            "MATCH (staff:Staff)-[:" + HAS_FAVOURITE_FILTERS + "]->(staffFavouriteFilter:StaffFavouriteFilter{deleted:false}) " +
            "WHERE id(staffFavouriteFilter) = {2} return staffFavouriteFilter")
    StaffFavouriteFilter getStaffFavouriteFiltersOfStaffInOrganizationById(Long userId, Long organizationId, Long staffFavouriteFilterId);

    @Query("MATCH (staffFavouriteFilter:StaffFavouriteFilter)-[r:" + FILTER_DETAIL + "]->(filterDetail:FilterSelection) WHERE id(staffFavouriteFilter)={0} \n" +
            "DELETE r,filterDetail")
    void detachStaffFavouriteFilterDetails(Long staffFavouriteFilterId);

    @Query("Match(organization:Organization)-[:" + HAS_EMPLOYMENTS + "]->(employments:Employment)-[:" + BELONGS_TO + "]->(staff:Staff{deleted:false}) where id(organization)={0} " +
            "match(staff)-[:" + HAS_CONTACT_DETAIL + "]->(contactDetail:ContactDetail) return  id(staff) as id, staff.firstName as firstName, " +
            " staff.lastName as lastName, contactDetail.privatePhone as privatePhone ")
    List<StaffPersonalDetailDTO> getAllStaffWithMobileNumber(long unitId);


    @Query("Match(o:Organization)-[:"+HAS_EMPLOYMENTS+"]->"+"(e:Employment)-[:"+BELONGS_TO+"]->(s:Staff) where id(o)={0} return s")
    List<Staff> getAllStaffByUnitId(long unitId);

    @Query("MATCH (org:Organization) WITH org\n" +
            "MATCH (org)-[:HAS_EMPLOYMENTS]-(employment:Employment)-[:BELONGS_TO]-(staff:Staff)-[:BELONGS_TO]->(user:User) WITH staff, user\n" +
            "MATCH (staff)-[:BELONGS_TO_STAFF]-(unitPosition:UnitPosition{deleted:false})-[:IN_UNIT]-(o:Organization)\n" +
            "with  collect({id: id(staff),firstName:staff.firstName,lastName:staff.lastName, gender :user.gender, pregnant:user.pregnant, dateOfBirth:user.dateOfBirth}) as staffData,o " +
            "RETURN  id(o) as unitId, staffData as staffList")
    List<UnitStaffQueryResult> getStaffListOfUnitWithBasicInfo();

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) where id(user)={0} AND id(organization)={1}  return staff")
    Staff findByUserId(Long userId, Long unitId);




    @Query("MATCH (staff:Staff)-[:"+BELONGS_TO+"]-(user:User)where id(staff)={0} with staff,user\n" +
            "MATCH (user)-[:"+BELONGS_TO+"]-(staffs:Staff)where id(staffs)<>{0} with staffs,user\n " +
            "MATCH (staffs)-[:"+BELONGS_TO+"]-(employment:Employment) with staffs,user,employment\n" +
            "WHERE (employment.mainEmploymentEndDate IS NULL OR employment.mainEmploymentEndDate>={1}) AND ((employment.mainEmploymentStartDate IS NOT NULL) AND({2} IS NULL OR employment.mainEmploymentStartDate<={2})) with user,employment,staffs\n" +
            "MATCH (employment)-[:"+HAS_EMPLOYMENTS+"]-(organization:Organization) RETURN employment,organization.name as organizationName")
    List<MainEmploymentQueryResult> getAllMainEmploymentByStaffId(Long staffId, Long mainEmploymentStartDate, Long mainEmploymentEndDate );

    @Query("MATCH(staff:Staff)-[:"+BELONGS_TO_STAFF+"]->(unitPosition:UnitPosition)-[:"+HAS_EXPERTISE_IN+"]->(expertise:Expertise) where id(expertise)={1} AND id(staff) IN {2}\n" +
            "MATCH(unitPosition)-[:"+IN_UNIT+"]-(organization:Organization) where id(organization)={0}   \n" +
            "AND unitPosition.startDateMillis<={3} AND  (unitPosition.endDateMillis IS NULL or unitPosition.endDateMillis>={3})  \n" +
            "return id(unitPosition) as id , id(staff) as staffId")
    List<StaffUnitPositionDetails> getStaffIdAndUnitPositionId(Long unitId, Long expertiseId, List<Long> staffIds,Long currentMillis);

    @Query("MATCH(staff:Staff) where id(staff) in {0} RETURN staff.email")
    List<String> getEmailsOfStaffByStaffIds(List<Long> staffIds);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization) where id(user)={0} AND id(organization)={1}  return id(staff)")
    Long findStaffIdByUserId(Long userId, Long unitId);

    @Query("MATCH(user:User)<-[:" + BELONGS_TO + "]-(staff:Staff)<-[:" + BELONGS_TO + "]-(employment:Employment)<-[:" + HAS_EMPLOYMENTS + "]-(organization:Organization{isKairosHub:true})-[:"+HAS_SUB_ORGANIZATION+"]-(org:Organization) where id(user)={0} AND id(org)={1}  return id(staff)")
    Long findHubStaffIdByUserId(Long userId, Long unitId);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) where id(user)={0} with staff\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition{deleted:false,published:true}) with staff,up \n" +
            "MATCH(up)-[: " + IN_UNIT + " ]-(org:Organization{deleted:false}) with staff,up,org\n" +
            "MATCH (up)-[: " + HAS_EXPERTISE_IN + "]-(exp:Expertise) with staff,up,org,exp \n"+
            "Optional match (org)-[:" + BELONGS_TO + "]-(reasonCode:ReasonCode{deleted:false}) where reasonCode.reasonCodeType={1} with staff,up,org,exp,reasonCode \n " +
            "RETURN id(staff) as staffId,id(org) as unitId,org.name as unitName,org.timeZone as timeZone,COLLECT(DISTINCT reasonCode) as reasonCodes,COLLECT({id:id(up),expertiseName:exp.name}) as unitPosition")
    List<StaffInformationQueryResult> getStaffAndUnitTimezoneByUserIdAndReasonCode(Long id, ReasonCodeType reasonCodeType);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) where id(user)={0} with staff\n" +
            "MATCH(staff)-[:" + BELONGS_TO_STAFF + "]-(up:UnitPosition{deleted:false,published:true}) with staff,up \n" +
            "MATCH(up)-[: " + IN_UNIT + " ]-(org:Organization{deleted:false}) RETURN id(staff) as staffId,id(org) as unitId ")
    List<StaffInformationQueryResult> getStaffIdsAndUnitByUserId(Long userId);

    @Query("Match(staff:Staff)-[rel:"+STAFF_HAS_EXPERTISE+"]-(exp:Expertise) where id(staff)={0} and id(exp)={1} set rel.expertiseStartDate = {2}  return staff,exp")
    void updateStaffExpertiseRelation(Long staffId,Long expertiseId,Long millis);

    @Query("MATCH (user:User)-[:" + BELONGS_TO + "]-(staff:Staff) where id(user)={0} with staff\n" +
            "match(staff)-[:" + BELONGS_TO + "]-(employment:Employment)-[:" + HAS_EMPLOYMENTS + "]-(org:Organization{deleted:false}) with staff,org\n"+
            "RETURN id(staff) as staffId,id(org) as unitId,org.name as unitName")
    List<StaffInformationQueryResult> getAllStaffsAndUnitDetailsByUserId(Long userId);

    @Query("Optional MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(e:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE staff.email=~{0} AND id(organization)={1} RETURN staff")
    Staff findStaffByEmailInOrganization(String email,Long unitId);

    @Query("MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE id(organization)={0} \n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) " +
            "Match (staff)-[:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(expertise) ={2} \n" +
            "return  distinct id(staff) as id,staff.firstName as firstName,staff.lastName as lastName,staff.userName as userName,user.cprNumber as cprNumber,user.gender as gender, {1} + staff.profilePic as profilePic")
    List<StaffPersonalDetailDTO> getAllStaffByUnitIdAndExpertiseId(Long unitId, String imageUrl,Long expertiseId);

    @Query("MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff) WHERE id(organization)={0} \n" +
            "Match (staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise) WHERE id(expertise) ={1} AND rel.unionRepresentative\n" +
            "SET rel.unionRepresentative=false " +
            "return count(rel)")
    int removePreviousUnionRepresentativeOfExpertiseInUnit(Long unitId, Long expertiseId);

    @Query("MATCH (staff:Staff),(expertise:Expertise) WHERE id(staff)={0} AND id(expertise) ={1} \n" +
            "Match (staff)-[rel:" + STAFF_HAS_EXPERTISE + "]->(expertise:Expertise)\n" +
            "SET rel.unionRepresentative=true " +
            "return count(rel) ")
    int assignStaffAsUnionRepresentativeOfExpertise(Long staffId, Long expertiseId);

    @Query("MATCH (organization:Organization),(expertise:Expertise) WHERE id(organization)={1} AND id(expertise) IN {0} " +
            "MATCH (organization)-[:"+HAS_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff)-[rel:" + STAFF_HAS_EXPERTISE + "{unionRepresentative:true}]->(expertise) \n" +
            " MATCH(staff)-[:"+BELONGS_TO+"]-(user:User)\n" +
            "return {id:id(staff),name:user.firstName} as staff,id(expertise) as expertiseId")
    List<ExpertiseLocationStaffQueryResult> findAllUnionRepresentativeOfExpertiseInUnit(List<Long> expertiseIds, Long unitId);

    @Query("MATCH (organization:Organization{deleted:false,isEnable:true})<-[:"+HAS_SUB_ORGANIZATION+"*]-(organizationHub:Organization{deleted:false,isEnable:true}) \n" +
            "-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+BELONGS_TO+"]-(staff:Staff)-[:"+BELONGS_TO+"]->(user:User)\n " +
            "WHERE id(organization)={0} AND organizationHub.isKairosHub=true AND id(user)={1} \n " +
            "RETURN staff")
    Staff getStaffByOrganizationHub(Long currentUnitId,Long userId);
}

