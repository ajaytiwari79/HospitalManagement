package com.kairos.persistence.repository.user.staff;

import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.StaffRelationship;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.client.ClientStaffRelation;
import com.kairos.persistence.model.user.client.ContactDetail;
import com.kairos.persistence.model.user.position.Position;
import com.kairos.persistence.model.user.skill.Skill;
import com.kairos.persistence.model.user.staff.*;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by prabjot on 24/10/16.
 */
@Repository
public interface StaffGraphRepository extends GraphRepository<Staff> {

    @Override
    List<Staff> findAll();

    Staff findByExternalId(Long externalId);

    @Query("MATCH (team:Team)-[:"+TEAM_HAS_MEMBER+"{isEnabled:true}]->(staff:Staff) where id(team)={0} return {id:id(staff),firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId, userName:staff.userName,profilePic: {1} + staff.profilePic } as data order by data.firstName")
    List<Map<String,Object>> getStaffByTeamId(long teamId, String imageUrl);

    @Query("MATCH (group:Group)-[:"+HAS_TEAM+"]->(team:Team)-[:"+TEAM_HAS_MEMBER+"]->(staff:Staff) where id(group)={0} return {id:id(staff),firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId,profilePic: {1} + staff.profilePic} as data order by data.firstName")
    List<Map<String,Object>> getStaffByGroupId(long groupId, String imageUrl);

    @Query("MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (organization:Organization)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(organization)={1} with staff\n " +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, staff\n" +
            "OPTIONAL Match (staff)-[:"+ENGINEER_TYPE+"]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "return {id:id(staff),name:staff.firstName+\" \" +staff.lastName,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId, age:user.age, gender:user.gender, profilePic:{2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) where id(unit)={1} with unitEmployment, staff\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, unitEmployment, staff\n" +
            "OPTIONAL Match (staff)-[:"+ENGINEER_TYPE+"]->(engineerType:EngineerType) with engineerType, staff, user\n" +
            "return {id:id(staff), name:staff.firstName+\" \" +staff.lastName, firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId, age:user.age, gender:user.gender, profilePic: {2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName")
    List<Map<String,Object>> getStaffWithBasicInfo(long organizationId, long unitId, String imageUrl);

    @Query("MATCH (organization:Organization)-[:"+HAS_GROUP+"]->(group:Group)-[:"+HAS_TEAM+"]->(team:Team)-[:"+TEAM_HAS_MEMBER+"]->(staff:Staff) where id(organization)={0} with collect(id(team)) as teams,staff\n" +
            "Match (staff)-[:"+STAFF_HAS_SKILLS+"]->(skill:Skill) with staff,teams,collect(id(skill)) as skills\n" +
            "return id(staff) as id,staff.firstName+\" \" +staff.lastName as name,staff.profilePic as profilePic,teams as teams,skills as skills order by name\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (staff:Staff)<-[:"+BELONGS_TO+"]-(employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={1}\n" +
            "Match (team:Team)-[:"+TEAM_HAS_MEMBER+"]->(staff) with staff,collect(id(team)) as teams\n" +
            "Match (staff)-[:"+STAFF_HAS_SKILLS+"]->(skill:Skill) with staff,teams,collect(id(skill)) as skills\n" +
            "return id(staff) as id,staff.firstName+\" \" +staff.lastName as name,staff.profilePic as profilePic,teams as teams,skills as skills order by name")
    List<StaffAdditionalInfoQueryResult> getStaffWithAdditionalInfo(long organizationId, long unitId);


    @Query("MATCH (unitEmployments:UnitEmployment)-[:PROVIDED_BY]->(organization:Organization) where id(organization)={0} with unitEmployments\n"+
            "MATCH (staffs:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployments) with staffs " +
            "OPTIONAL MATCH (staffs)-[:STAFF_HAS_SKILLS{isEnabled:true}]->(skills:Skill{isEnabled:true}) \n"+
            "with staffs,collect(id(skills)) as skills OPTIONAL MATCH (teams:Team)-[:TEAM_HAS_MEMBER{isEnabled:true}]->(staffs) \n"+
            "with staffs,skills,collect(id(teams)) as teams \n" +
            "return id(staffs) as id,staffs.firstName+\" \" +staffs.lastName as name,staffs.profilePic as profilePic,teams,skills order by name")
    List<StaffAdditionalInfoQueryResult> getStaffAndCitizenDetailsOfUnit(long unitId);

    @Query("MATCH (staff:Staff) where id(staff)={0} Match (team)-[r:TEAM_HAS_MEMBER]->(staff) SET r.isEnabled=false return r")
    List<StaffRelationship> removeStaffFromAllTeams(long staffId);

    @Query("Match (team:Team),(staff:Staff) where id(team) IN {1} AND id(staff)={0} CREATE UNIQUE (team)-[:TEAM_HAS_MEMBER]-(staff) return team")
    Staff editStaffWorkPlaces(long staffId, List<Long> staffIds);

    @Query("Match (unit:Organization),(staff:Staff) where id(staff)={0} AND id(unit)={1} with staff,unit\n" +
            "Match (unit)-[orgSkillRelation:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill:Skill{isEnabled:true}) with skill,staff, orgSkillRelation\n" +
            "OPTIONAL MATCH (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill{isEnabled:true}) with skill,r,orgSkillRelation\n" +
            "Match (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with skill,skillCategory,r,orgSkillRelation\n" +
            "return {children:collect({id:id(skill),name:orgSkillRelation.customName,isSelected:r.isEnabled}),id:id(skillCategory),name:skillCategory.name} as data")
    List<Map<String,Object>> getSkills(long staffId, long unitId);

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

    @Query("MATCH (c:Staff) where c.email={0}  return c")
    Staff findByEmail(String email);


    @Query("MATCH (s:Staff) where s.organizationId={0} return s")
    List<Staff> getUploadedStaffByOrganizationId(Long organizationId);

    @Query("MATCH (c:Client)-[r:SERVED_BY_STAFF]-(s:Staff) WHERE id(c)={0} and id(s)={1} return  r")
    ClientStaffRelation checkRestrictedStaff(Long clientId, long id);

    @Query("Match (organization:Organization),(staff:Staff) where id(organization)={0} AND id(staff)={1} with organization,staff\n" +
            "Match (employment:Employment)-[:BELONGS_TO]->(staff) with employment,organization\n" +
            "Match (employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment)-[:PROVIDED_BY]->(organization) with unitEmployment\n" +
            "Match (unitEmployment)-[:HAS_PARTIAL_LEAVES]->(partialLeave:PartialLeave)\n" +
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

    @Query("Match (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff{kmdExternalId:{1}}) where id(organization)={0} return count(staff)>0")
    Boolean isStaffExist(long organizationId, long externalId);


    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) with employment,unit\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff{email:{2}}) with employment\n" +
            "Match (empoyment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment{isUnitManagerEmployment:true})-[:PROVIDED_BY]->(unit) with unitEmployment with count(unitEmployment) as count return count")
    int countOfUnitEmployment(long organizationId, long unitId, String email);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment{isUnitManagerEmployment:true})-[:PROVIDED_BY]->(unit) with employment,unitEmployment\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitEmployment\n" +
            "Match (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "\n" +
            "optional Match (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "return {id:id(staff),accessGroupId:id(accessGroup),email:staff.email,firstName:staff.firstName,lastName:staff.lastName,contactDetail:{landLinePhone:contactDetail.landLinePhone,mobilePhone:contactDetail.mobilePhone}} as data")
    List<Map<String,Object>> getUnitManagers(long organizationId, long unitId);


    @Query("MATCH (s:Staff) where id(s)={0} DELETE s")
    void deleteStaffById(long staffId);

    @Query("MATCH (s:Staff)-[rel]-(e:Employment) where id(s)={0} AND id(e)={1} DELETE rel")
    void deleteStaffEmployment(long staffId, long employmentId);

    @Query("MATCH (s:Staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) where id(s)={0} return contactDetail")
    ContactDetail getContactDetail(long staffId);

    @Query("Match (organization:Organization) where id(organization)={0}\n" +
            "Match (organization)-[:HAS_GROUP]->(g:Group)-[:HAS_TEAM]->(team:Team) \n" +
            "Match (team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(staff) IN {1} \n" +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with collect({id:id(skill),name:skill.name}) as skills,staff,team\n" +
            "return {id:id(team),name:team.name,staffList:collect({id:id(staff), name:staff.firstName+\" \" +staff.lastName,cprNumber:staff.cprNumber, profilePic: staff.profilePic, skills:case when skills[0].id is null then [] else skills end})} as data")
    List<Map<String,Object>> getTeamStaffList(Long organizationId, List staffIds);

    @Query("Match (staff:Staff) where id(staff) IN {0} with staff\n" +
            "OPTIONAL MATCH (staff)-[r:STAFF_HAS_SKILLS]->(skill) with collect({id:id(skill),name:skill.name}) as skills,staff\n" +
            "return {skills:case when skills[0].id is null then [] else skills end,id:id(staff),name:staff.firstName+\" \" +staff.lastName,cprNumber:staff.cprNumber, profilePic: staff.profilePic} as data")
    List<Map<String,Object>> getSkillsOfStaffs(List<Long> staffIds);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:"+HAS_EMPLOYMENTS+"]->(employment:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployment:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit) with employment,unitEmployment\n" +
            "MATCH (employment)-[:"+BELONGS_TO+"]->(staff:Staff) with staff,unitEmployment\n" +
            "Match (unitEmployment)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{typeOfTaskGiver:true}) with accessGroup,staff\n" +
            "optional Match (staff)-[:"+CONTACT_DETAIL+"]->(contactDetail:ContactDetail)\n" +
            "return {id:id(staff),accessGroupId:id(accessGroup),email:staff.email,firstName:staff.firstName,lastName:staff.lastName,fmVTID:staff.visitourId,contactDetail:{landLinePhone:staff.landLinePhone,mobilePhone:staff.mobilePhone}} as data")
    List<Map<String,Object>> getFieldStaff(long organizationId, long unitId);

    @Query("Match (unit:Organization),(staff:Staff) where id(unit)={0} AND id(staff)={1}\n" +
            "Match (unit)-[r:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill:Skill)<-[staffSkillRel:"+STAFF_HAS_SKILLS+"{isEnabled:true}]-(staff) where r.visitourId is not null\n" +
            "return CASE staffSkillRel.skillLevel WHEN 'BASIC' THEN r.visitourId + '(1)' WHEN 'ADVANCE' THEN r.visitourId + '(2)' WHEN 'EXPERT' THEN r.visitourId + '(3)' ELSE r.visitourId +'(2)' END AS result")
    List<String> getStaffVisitourIdWithLevel(long unitId, long staffId);

    List<Staff> findByUserName(String userName);

    @Query("Match (emp:Employment)-[:"+BELONGS_TO+"]->(s:Staff) where id(s)={0} with emp\n" +
            "Match (emp)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={1} with unitEmp\n" +
            "Match (unitEmp)-[:"+HAS_ACCESS_PERMISSION+"]->(accessPermission:AccessPermission)-[:"+HAS_ACCESS_GROUP+"]->(accessGroup:AccessGroup{typeOfTaskGiver:true})\n" +
            "return count(accessGroup) as accessGroup")
    int checkIfStaffIsTaskGiver(long staffId, long unitId);

    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1    Create Unique (c)-[r:SERVED_BY_STAFF{type:'PREFERRED'}]->(s) return count(r)")
    int createClientStaffPreferredRelation(Long clientId, List<Long> staffIds);


    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  OPTIONAL MATCH (c)-[r1:SERVED_BY_STAFF]->(s) delete r1  Create Unique (c)-[r:SERVED_BY_STAFF{type:'FORBIDDEN'}]->(s) return count(r)")
    int createClientStaffForbidRelation(Long clientId, List<Long> staffIds);

    @Query("MATCH (c:Client) , (s:Staff) where id(c)={0}  AND id(s) IN {1}  MATCH  (c)-[r:SERVED_BY_STAFF]->(s)  delete r return count(r)")
    int  deleteClientStaffRelation(Long clientId, List<Long> staffIds);

    @Query("Match (staff:Staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill:Skill) where id(staff)={0} AND id(skill)={1} return count(r) as countOfRel")
    int staffHasAlreadySkill(long staffId, long skillId);

    @Query("Match (staff:Staff),(skill:Skill) where id (staff)={0} AND id(skill) IN {1} with staff,skill\n" +
            "Merge (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill)\n" +
            "ON CREATE SET r.creationDate={2},r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5}\n" +
            "ON MATCH SET r.lastModificationDate={3},r.startDate={3},r.endDate=0,r.skillLevel={4},r.isEnabled={5} return true")
    void addSkillInStaff(long staffId, List<Long> skillId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel, boolean isEnabled);

    @Query("Match (staff:Staff),(skill:Skill) where id(staff)={0} and id(skill) IN {1}\n" +
            "Match (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill)-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory) with skill, staff, skillCategory, r\n" +
            "Match (organization:Organization)-[orgHasSkill:"+ORGANISATION_HAS_SKILL+"]->(skill:Skill) where id(organization)={2} with skill, staff, skillCategory,orgHasSkill, r \n" +
            "return {id:id(r),skillId:id(skill),name:orgHasSkill.customName,skillCategory:skillCategory.name,startDate:r.startDate,endDate:r.endDate,visitourId:skill.visitourId,lastSyncInVisitour:r.lastModificationDate,status:r.isEnabled,level:r.skillLevel} as data")
    List<Map<String,Object>> getStaffSkillInfo(long staffId, List<Long> skillId, long unitId);

    @Query("Match (staff:Staff) where id(staff)={0} with staff\n" +
            "Match (expertise:Expertise)-[r:"+EXPERTISE_HAS_SKILLS+"{isEnabled:true}]->(skill:Skill) where id(expertise)={1} with staff,skill\n" +
            "MERGE (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill)\n" +
            "ON CREATE SET r.creationDate ={2},r.lastModificationDate ={3},r.isEnabled=true,r.skillLevel={4}\n" +
            "ON MATCH SET r.lastModificationDate = {3},r.skillLevel={4},r.isEnabled=true return true")
    void updateSkillsByExpertise(long staffId, long expertiseId, long creationDate, long lastModificationDate, Skill.SkillLevel skillLevel);

    @Query("Match (staff:Staff) where id(staff)={0} with staff\n" +
            "Match (expertise:Expertise)-[r:"+EXPERTISE_HAS_SKILLS+"{isEnabled:true}]->(skill:Skill) where id(expertise)={1} with staff,skill\n" +
            "Match (staff)-[r:"+STAFF_HAS_SKILLS+"]->(skill)\n" +
            "set r.isEnabled=false return true")
    void removeSkillsByExpertise(long staffId, long expertiseId);

    @Query("MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "OPTIONAL MATCH (staff:Staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) where id(unit)={1} with unitEmployment, staff\n" +
            "OPTIONAL MATCH (staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_EMPLOYMENTS]->(subUnitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) with unit, unitEmployment, staff\n" +
            "OPTIONAL MATCH (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup, unit, staff\n" +
            "OPTIONAL MATCH (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) with contactDetail, staff, accessGroup, unit\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, contactDetail, staff, accessGroup, unit\n" +
            "OPTIONAL Match (staff)-[:"+ENGINEER_TYPE+"]->(engineerType:EngineerType) with engineerType, user, contactDetail, staff, accessGroup, unit\n" +
            "MATCH (organization:Organization)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(organization)={1} return {id:id(staff),name:staff.firstName+\" \" +staff.lastName,firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId, userName:staff.userName, workPlaces:collect(distinct id(unit)), roles:collect(distinct id(accessGroup)), phoneNumber:contactDetail.mobilePhone, age:user.age, gender:user.gender, profilePic: {2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:HAS_EMPLOYMENTS]->(employment:Employment) where id(organization)={0} with employment\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) where id(unit)={1} with unitEmployment, staff\n" +
            "OPTIONAL MATCH (staff)<-[:BELONGS_TO]-(employment)-[:HAS_UNIT_EMPLOYMENTS]->(subUnitEmployment:UnitEmployment)-[:PROVIDED_BY]->(unit:Organization) with unit, staff, unitEmployment\n" +
            "OPTIONAL MATCH (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup, unit, staff\n" +
            "OPTIONAL MATCH (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)  with contactDetail, staff, accessGroup, unit\n" +
            "MATCH (staff)-[:BELONGS_TO]->(user:User) with user, contactDetail, staff, accessGroup, unit\n" +
            "OPTIONAL Match (staff)-[:"+ENGINEER_TYPE+"]->(engineerType:EngineerType) with engineerType, user, contactDetail, staff, accessGroup, unit\n" +
            "return {id:id(staff), name:staff.firstName+\" \" +staff.lastName, firstName:staff.firstName,lastName:staff.lastName,familyName:staff.familyName,cprNumber:staff.cprNumber,visitourId:staff.visitourId, userName:staff.userName, workPlaces:collect(distinct id(unit)), roles:collect(distinct id(accessGroup)), phoneNumber:contactDetail.privatePhone, age:user.age, gender:user.gender, profilePic: {2} + staff.profilePic, engineerType:id(engineerType)} as data order by data.firstName")
    List<Map<String,Object>> getStaffInfoForFilters(long organizationId, long unitId, String imageUrl);

    Staff findByKmdExternalId(Long kmdExternalId);


    @Query("Match (e:Employment)-[:"+BELONGS_TO+"]->(staff:Staff) where id(staff)={1} with e,staff\n" +
            "Match (e)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmp:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={0} return staff")
    Staff getStaffByUnitId(long unitId, long staffId);

    @Query("Match (unitEmployment:UnitEmployment)-[:PROVIDED_BY]->(organization:Organization) where id(organization)={0} with unitEmployment\n" +
            "Match (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup{name:\"COUNTRY_ADMIN\"}) with unitEmployment\n" +
            "Match (employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment) with employment,unitEmployment\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff\n" +
            "return id(staff)")
    List<Long> getCountryAdminIds(long organizationId);

    @Query("MATCH (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} with organization,unit\n" +
            "match (organization)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployment:UnitEmployment{isUnitManagerEmployment:true})-[:PROVIDED_BY]->(unit) with employment,unitEmployment\n" +
            "MATCH (employment)-[:BELONGS_TO]->(staff:Staff) with staff,unitEmployment\n" +
            "Match (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessGroup,staff\n" +
            "\n" +
            "optional Match (staff)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail)\n" +
            "return id(staff)")
    List<Long> getUnitManagersIds(long organizationId, long unitId);


    @Query("match(user:User)  where id(user)={1} \n" +
            "match(staff:Staff)-[:BELONGS_TO]->(user) \n" +
            "optional MATCH (staff)-[:"+HAS_CONTACT_ADDRESS+"]-(contactAddress:ContactAddress)\n"+
            "return  id(staff) as id,user.gender as gender,staff.profilePic as profilePic, contactAddress.city as city,contactAddress.province as province , staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,staff.organizationId as organizationId,staff.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName")
    List<StaffPersonalDetailDTO> getStaffInfoById(long unitId, long staffId);

    @Query("MATCH (unitEmployments:UnitEmployment)-[:"+PROVIDED_BY+"]->(organization:Organization) where id(organization)={0} with unitEmployments ,organization\n" +
            "MATCH (staff:Staff)<-[:"+BELONGS_TO+"]-(employment:Employment)-[:"+HAS_UNIT_EMPLOYMENTS+"]->(unitEmployments)\n" +
            "MATCH (staff)-[:"+BELONGS_TO+"]-(user:User)\n"+
            "MATCH (staff)-[:"+HAS_CONTACT_ADDRESS+"]-(contactAddress:ContactAddress)\n"+
            "return  id(staff) as id, user.gender as gender,staff.profilePic as profilePic, contactAddress.city as city,contactAddress.province as province ,staff.firstName as firstName,staff.lastName as lastName,staff.employedSince as employedSince,staff.badgeNumber as badgeNumber, staff.userName as userName,staff.externalId as externalId,staff.organizationId as organizationId,staff.cprNumber as cprNumber,staff.visitourTeamId as visitourTeamId,staff.familyName as familyName")
    List<StaffPersonalDetailDTO> getAllStaffByUnitId(long unitId);

    @Query("MATCH (staff:Staff)-[:ENGINEER_TYPE]->(engineerType:EngineerType) where id(staff)={0} return id(engineerType)")
    Long getEngineerTypeId(Long staffId);

    @Query("MATCH (staff:Staff)-[:HAS_EXPERTISE_IN]->(expertise:Expertise) where id(staff)={0} return id(expertise)")
    Long getExpertiseId(Long staffId);

    @Query("MATCH (staff:Staff)-[:LANGUAGE]->(language:Language) where id(staff)={0} return id(language)")
    Long getLanguageId(Long staffId);

    @Query("Match (team:Team)-[:TEAM_HAS_MEMBER]->(staff:Staff) where id(staff)= {1} AND id(team)={0}  return staff ")
    Staff getTeamStaff(Long teamId, Long staffId);

    @Query("Match (unitEmployment:UnitEmployment)-[:"+PROVIDED_BY+"]->(unit:Organization) where id(unit)={1} with unitEmployment\n" +
            "Match (unitEmployment)<-[:"+HAS_UNIT_EMPLOYMENTS+"]-(employment:Employment)-[:"+BELONGS_TO+"]->(staff:Staff{externalId:{0}}) return count(staff)>0")
    Boolean staffAlreadyInUnit(Long externalId,Long unitId);

    @Query("Match (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff{externalId:{1}}) \n" +
            "where id(organization)={0} with staff\n" +
            "optional match (staff)-[:"+HAS_CONTACT_ADDRESS+"]->(contactAddress:ContactAddress) with staff,contactAddress\n" +
            "optional match (staff)-[:"+HAS_CONTACT_DETAIL+"]->(contactDetail:ContactDetail) with staff,contactDetail,contactAddress\n" +
            "return staff,id(contactAddress) as contactAddressId,id(contactDetail) as contactDetailId")
    StaffQueryResult getStaffByExternalIdInOrganization(Long organizationId, Long externalId);

    @Query("MATCH (unitEmployments:UnitEmployment)-[:PROVIDED_BY]->(organization:Organization) where id(organization)={0} with unitEmployments ,organization\n" +
            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployments)\n" +
            "return  id(staff) as id, staff.firstName as firstName,staff.lastName as lastName")
    List<StaffPersonalDetailDTO> getAllStaffDetailByUnitId(long unitId);

    @Query("MATCH (unitEmployments:UnitEmployment)-[:PROVIDED_BY]->(organization:Organization) where id(organization)={0} with unitEmployments ,organization\n" +

            "MATCH (o:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType) WHERE id(o)=71 with et\n" +
            "OPTIONAL MATCH (o)-[r:EMPLOYMENT_TYPE_SETTINGS]->(et) with \n" +
            "collect(CASE WHEN r IS NULL AND  et.allowedForContactPerson =true THEN  {id:id(et),allowedForContactPerson:et.allowedForContactPerson} \n" +
            "ELSE {id:id(et),allowedForContactPerson:r.allowedForContactPerson} END) as employmentTypeSettings with filter\n" +
            "(x IN employmentTypeSettings WHERE x.allowedForContactPerson=true) as filteredEmploymentType with extract(n IN filteredEmploymentType| n.id) AS extractedEmploymentTypeId \n"+

            "MATCH (staff:Staff)<-[:BELONGS_TO]-(employment:Employment)-[:HAS_UNIT_EMPLOYMENTS]->(unitEmployments)\n" +
            "match (unitEmployments)-[:HAS_POSITION]->(p:Position)-[:"+HAS_EMPLOYMENT_TYPE+"]->(et:EmploymentType) WHERE id(et) IN extractedEmploymentTypeId\n" +
            "return distinct id(staff) as id, staff.firstName as firstName,staff.lastName as lastName")
    List<StaffPersonalDetailDTO> getAllMainEmploymentStaffDetailByUnitId(long unitId);


    @Query("MATCH (staff:Staff)-[:"+HAS_FAVOURITE_FILTERS+"]->(staffFavouriteFilters:StaffFavouriteFilters{enabled:true}) where id(staff)={0} with staffFavouriteFilters\n"+
            "MATCH (staffFavouriteFilters)-[:"+FILTER_BY_PAGE+"]->(accessPage:AccessPage) where accessPage.moduleId={1} return staffFavouriteFilters\n" )
    List<StaffFavouriteFilters> getStaffFavouriteFiltersByStaffAndView(Long staffId, String moduleId);

    @Query("MATCH (staff:Staff)-[:"+HAS_FAVOURITE_FILTERS+"]->(staffFavouriteFilters:StaffFavouriteFilters) where id(staff)={0} AND id(staffFavouriteFilters)={1} return staffFavouriteFilters" )
    StaffFavouriteFilters getStaffFavouriteFiltersById(Long staffId, Long staffFavouriteFiltersId);


    @Query("MATCH (u:User)-[:BELONGS_TO]-(s:Staff) where id(u)={0} return s ")
    Staff getStaffByUserId(Long id);

    @Query("Match (organization:Organization)-[:"+HAS_EMPLOYMENTS+"]->(emp:Employment)-[:"+BELONGS_TO+"]->(staff:Staff) where id(organization)={1}" +
            "Match (staff)-[:"+BELONGS_TO+"]->(user:User) where id(user)={0} return staff")
    Staff getStaffByUserId(Long userId,Long parentOrganizationId);




}
