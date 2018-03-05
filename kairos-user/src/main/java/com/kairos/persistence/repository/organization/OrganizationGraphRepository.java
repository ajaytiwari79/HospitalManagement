package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.enums.OrganizationLevel;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.user.client.Client;
import com.kairos.persistence.model.user.client.ContactAddress;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.position_code.PositionCode;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Interface for CRUD operation on Organization
 */
@Repository
public interface OrganizationGraphRepository extends Neo4jBaseRepository<Organization, Long>, CustomOrganizationGraphRepository {

    @Query("MATCH (o:Organization {isEnable:true}) return {name:o.name, id:id(o)} as organization")
    List<Map<String, Object>> findAllOrganizations();

    Organization findByName(String name);

    Organization findByEmail(String email);

    Organization findByExternalId(String externalId);

    List<Organization> findByOrganizationLevel(OrganizationLevel organizationLevel);

    @Query("MATCH (t:TimeCare) WHERE t.timeCareExternalId = {0} WITH t.organizationId AS organizationId \n" +
            "OPTIONAL MATCH (o:Organization) WHERE id(o)= organizationId RETURN o")
    Organization findByTimeCareId(String timeCareId);

    @Query("MATCH (o:Organization {isEnable:true} ),(o1:Organization) where id(o)={0} AND id(o1)={1} create (o)-[:HAS_SUB_ORGANIZATION]->(o1) return o1")
    Organization createChildOrganization(long parentOrganizationId, long childOrganizationId);

    @Query("match(n:Organization {isEnable:true}),(d:Department),(u:User) where id(n)={0} And id(d)={1} And id(u)={2}  create(d)-[:has_staff]->(u)")
    Organization addStaff(long organzationId, long departmentId, long userId);

    @Query("match(d:Department {isEnable:true} ),(t:Team) where id(d)={0} and id(t)in {1} create(d)-[:HAS_MANAGE]->(t)")
    Department linkDeptWithTeams(long departmentId, List<Long> childIds);

    @Query("MATCH (o:Organization)-[:HAS_GROUP]->(g:Group) where id(o)={0} return collect({id:id(g),name:g.name}) as groups")
    List<Map<String, Object>> getGroups(long id);

    @Query("MATCH (o:Organization {isEnable:true} )-[:" + HAS_GROUP + "]->(g:Group {isEnable:true}) where id(o)={0} AND id(g) = {1} return g")
    Group getGroups(Long organizationId, Long groupId);

    @Query("match (n:Organization) where id(n)={0} with n " +
            "match (n)<-[:HAS_SUB_ORGANIZATION*]-(org:Organization{isParentOrganization:true}) return org limit 1")
    Organization getParentOfOrganization(Long organizationId);

    // Skill
    @Query("Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(organization)={0} with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization\n" +
            "Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with\n" +
            " {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            " return {availableSkills:collect(availableSkills)} as data\n" +
            " UNION\n" +
            " Match (unit:Organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(unit)={1} with subType,unit\n" +
            " Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,unit\n" +
            " Match (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,unit,r\n" +
            " Match (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:r.customName,visitourId:r.visitourId, customName:r.customName, description:skill.description,isEdited:true})} as selectedSkills\n" +
            " return {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfChildOrganization(long organizationId, long unitId);

    /*@Query("Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(organization)={0} with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization\n" +
            "Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with\n" +
            " {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            " return {availableSkills:collect(availableSkills)} as data\n" +
            " UNION\n" +
            " Match (unit:Organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(unit)={1} with subType,unit\n" +
            " Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,unit\n" +
            " Match (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,unit,r\n" +
            " Match (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:skill.name,visitourId:r.visitourId, customName:r.customName, description:skill.description,isEdited:true})} as selectedSkills\n" +
            " return {selectedSkills:collect(selectedSkills)} as data")*/
    @Query("Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(organization)={0} with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,organization\n" +
            "Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with DISTINCT skill,r, organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=true with  skill,r,organization,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(organization) with  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +

            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with\n" +
            " {id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags+otags})} as availableSkills\n" +
            " return {availableSkills:collect(availableSkills)} as data\n" +
            " UNION\n" +
            " Match (unit:Organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(unit)={1} with subType,unit\n" +
            " Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) with distinct skill,unit\n" +
            " Match (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,unit,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=unit.showCountryTags with  skill,unit,r,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(unit) with  skill,r,unit,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            " Match (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:skill.name,visitourId:r.visitourId, customName:r.customName, description:skill.description,isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            " return {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfChildOrganizationWithActualName(long organizationId, long unitId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) with DISTINCT skill,organization\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with distinct skill,skillCategory,organization\n" +
            "optional Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) with\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            "return {availableSkills:collect(availableSkills)} as data\n" +
            "UNION\n" +
            "Match (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) where id(organization)={0} with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) with distinct skill,organization\n" +
            "Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId, customName:r.customName, isEdited:true})} as selectedSkills\n" +
            "return {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfParentOrganization(long unitId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) with DISTINCT skill,organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=true with  skill,organization,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with distinct skill,skillCategory,organization,ctags\n" +
            "optional Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) with\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags})} as availableSkills\n" +
            "return {availableSkills:collect(availableSkills)} as data\n" +
            "UNION\n" +
            "Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) where id(organization)={0} with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:EXPERTISE_HAS_SKILLS{isEnabled:true}]->(skill:Skill) with distinct skill,organization\n" +
            "Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) with skill,r,organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=organization.showCountryTags with  skill,organization,r,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + ORGANIZATION_HAS_TAG + "]-(organization) with  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:collect({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId, customName:r.customName, isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            "return {selectedSkills:collect(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfParentOrganizationWithActualName(long unitId);

    @Query("MATCH (g:Group)-[:" + HAS_TEAM + "]-(t:Team) where id(t) ={0}  with g as group MATCH(group)-[:" + GROUP_HAS_SKILLS + "]->(s:Skill) " +
            " with s AS skill " +
            "MATCH (sc:SkillCategory)-[:" + HAS_CATEGORY + "]-(skill) return  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:collect({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            "}) " +
            "} AS skillList ")
    List<Map<String, Object>> getTeamGroupSkill(Long teamId);

    @Query("MATCH (o:Organization)-[:CONTACT_ADDRESS]->(ca:ContactAddress) where id(o)={0} return ca")
    ContactAddress getOrganizationAddressDetails(Long organizationId);

    @Query("MATCH (o:Organization)-[:" + HAS_GROUP + "]->(g:Group) where id(g) ={0}  with o as org " +
            "MATCH (org)-[:" + PROVIDE_SERVICE + "]->(s:OrganizationService) with s " +
            "MATCH (os:OrganizationService)-[:ORGANIZATION_SUB_SERVICE]-(s)" +
            "return { " +
            "id:id(os), " +
            "name:os.name, " +
            "subServices: collect({ " +
            " id:id(s), " +
            " name:s.name " +
            "}) " +
            "} as serviceList ")
    List<Map<String, Object>> getGroupOrganizationServices(Long groupId);


    @Query("MATCH (g:Group)-[:" + HAS_TEAM + "]->(t:Team) where id(t) ={0} with g as grp " +
            "MATCH (grp)-[:" + GROUP_HAS_SERVICES + "]->(s:OrganizationService)  with s as ss" +
            " MATCH (os:OrganizationService)-[:ORGANIZATION_SUB_SERVICE]-(ss) " +
            "return { id:id(os), name:os.name, subServices:" +
            " collect({  id:id(ss), " +
            " name:ss.name }) } as serviceList ")
    List<Map<String, Object>> getTeamGroupServices(Long teamId);


    // Services
    @Query("MATCH (g:Group)-[:" + GROUP_HAS_SERVICES + "]-(os:OrganizationService) where id(g) = {0} with os as ss " +
            "MATCH (os:OrganizationService)-[:" + ORGANIZATION_SUB_SERVICE + "]-(ss) " +
            "return { " +
            "id:id(os), " +
            "name:os.name, " +
            "subServices: collect({ " +
            " id:id(ss), " +
            " name:ss.name " +
            "}) " +
            "} as serviceList")
    List<Map<String, Object>> getGroupAllSelectedServices(Long groupId);

    @Query("MATCH(t:Team)-[:" + TEAM_HAS_SERVICES + "]-(os:OrganizationService) where id(t)={0} with os as ss " +
            "MATCH (os:OrganizationService)-[:" + ORGANIZATION_SUB_SERVICE + "]-(ss) " +
            " return { " +
            " id:id(os), " +
            " name:os.name, " +
            " subServices: collect({ " +
            " id:id(ss), " +
            " name:ss.name " +
            "}) " +
            "} as serviceList")
    List<Map<String, Object>> getTeamAllSelectedSubServices(Long teamId);

    @Query("MATCH (c:Client)-[:GET_SERVICE_FROM]->(o:Organization)  where id(o)= {0} with c\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) with contactDetail, ca, c\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(civilianStatus:CitizenStatus) with civilianStatus, contactDetail, ca, c\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,  civilianStatus, contactDetail, ca, c\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c) , gender:c.gender, cprNumber:c.cprNumber , healthStatus:c.healthStatus,citizenDead:c.citizenDead, phoneNumber:contactDetail.mobilePhone, clientStatus:id(civilianStatus), " +
            "address:ca.houseNumber+\" \" +ca.street1, lat:ca.latitude, lng:ca.longitude, profilePic: {1} + c.profilePic, age:c.age, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganization(Long organizationId, String imageUrl);

    @Query("MATCH (c:Client)-[:GET_SERVICE_FROM]-(o:Organization) where id(o)= {0} return {firstName:c.firstName, lastName:c.lastName,id:id(c) , gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead}  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganizationForReport(Long organizationId);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) where id(o)= {0}  with c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail)  with cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) with cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,cs,cd,ca,c,r\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganizationExcludeDead(Long organizationId, String serverImageUrl);


    @Query("MATCH (c:Client{citizenDead:false}) WHERE id(c) IN {0} return {name:c.firstName+\" \" +c.lastName,id:id(c) , gender:c.gender, cprNumber:c.cprNumber  , citizenDead:c.citizenDead }  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsByClintIdList(List<Long> clientIds);

    @Query("MATCH (c:Client)-[:" + GET_SERVICE_FROM + "]-(o:Organization) where id(o)= {0} with c as client MATCH " +
            " (client)-[:HAS_HOME_ADDRESS]-(ca:ContactAddress) return {id:id(client), isHouseHolder: client.peopleInHousehold, name:client.firstName,address:id(ca),lat:ca.latitude,lon:ca.longitude} as result")
    List<Map<String, Object>> getClientsOfOrganizationForMobile(Long organizationId);

    @Query("MATCH (organization:Organization)-[:HAS_SUB_ORGANIZATION]->(unit:Organization) where id(organization)={0} with unit  " +
            "OPTIONAL MATCH (unit)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress) with " +
            "contactAddress,unit " +
            "Match (contactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with zipCode,contactAddress,unit " +
            "return collect({id:id(unit),name:unit.name,shortName:unit.shortName,contactAddress:case when contactAddress is null then [] else {id:id(contactAddress),city:contactAddress.city,longitude:contactAddress.longitude,latitude:contactAddress.latitude, " +
            "street1:contactAddress.street1,zipCodeId:id(zipCode),floorNumber:contactAddress.floorNumber, " +
            "houseNumber:contactAddress.houseNumber,province:contactAddress.province,country:contactAddress.country,regionName:contactAddress.regionName, " +
            "municipalityName:contactAddress.municipalityName} end}) AS unitList")
    List<Map<String, Object>> getUnits(long organizationId);

    @Query("Match (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) where id(o)={0} AND id(os)={1} SET r.isEnabled=false return r")
    void removeServiceFromOrganization(long unitId, long serviceId);

    @Query("Match (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) where id(o)={0} AND id(os)={1} SET r.customName=os.name, r.isEnabled=true return r")
    void updateServiceFromOrganization(long unitId, long serviceId);

    @Query("Match (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) where id(o)={0} AND id(os)={1} return count(r) as countOfRel")
    int isServiceAlreadyExist(long unitId, long serviceId);

    @Query("MATCH (o:Organization)-[:" + ORGANIZATION_HAS_DEPARTMENT + "]->(dept:Department) where id(o)={0} return dept")
    List<Department> getAllDepartments(Long organizationId);


    @Query("MATCH (o:Organization)-[:" + ORGANIZATION_HAS_DEPARTMENT + "]-(d:Department) where id(d)={0} return id(o)")
    Long getOrganizationByDepartmentId(Long departmentId);

    @Query("MATCH (o:Organization)-[:" + HAS_SUB_ORGANIZATION + "*..4]-(co:Organization) WHERE id(o)={0}  RETURN " +
            "collect ({name:co.name,id:id(co),level:co.organizationLevel}) as organizationList")
    List<Map<String, Object>> getOrganizationChildList(Long id);


    @Query("MATCH (o:Organization)-[:HAS_GROUP]-(g:Group) where id(o)={0} with g as grp MATCH (grp)-[:HAS_TEAM]-(t:Team) return { id:id(t) , name:t.name} as result")
    List<Map<String, Object>> getUnitTeams(Long unitId);

    @Query("MATCH (o:Organization {isEnable:true})-[:HAS_SETTING]-(os:OrganizationSetting) where id(o)={0} WITH os as setting MATCH (setting)-[:OPENING_HOUR]-(oh:OpeningHours) return oh order by oh.index")
    List<OpeningHours> getOpeningHours(Long organizationId);

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:false})-[:" + BELONGS_TO + "]->(c:Country)  where id(c)={0} with org\n" +
            "Match (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) with collect(id(ot)) as organizationTypeIds,org\n" +
            "optional match (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with  collect(id(subType)) as organizationSubTypeIds,organizationTypeIds,org\n" +
            "MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) with municipality,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode\n" +
            "MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) with collect(id(businessType)) as businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality\n" +
            "optional MATCH (org)-[:" + HAS_LEVEL + "]-(level:Level{isEnabled:true}) with level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality\n" +
            "return collect({id:id(org),levelId:id(level),businessTypeIds:businessTypeIds,typeId:organizationTypeIds,subTypeId:organizationSubTypeIds,name:org.name,prekairos:org.isPrekairos,kairosHub:org.isKairosHub,description:org.description,externalId:org.externalId,homeAddress:{houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,city:contactAddress.city,zipCode:id(zipCode),regionName:contactAddress.regionName,province:contactAddress.province,municipalityName:contactAddress.municipalityName,isAddressProtected:contactAddress.isAddressProtected,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street1:contactAddress.street1,municipalityId:id(municipality)}}) as organizations")
    OrganizationQueryResult getParentOrganizationOfRegion(long countryId);

    @Query("Match (country:Country) where id(country)={0} with country\n" +
            "MATCH (bt:BusinessType{isEnabled:true})-[:BELONGS_TO]->(country) with collect(bt) as bt,country\n" +
            "MATCH (ot:OrganizationType{isEnable:true})-[:BELONGS_TO]->(country) WITH ot,bt\n" +
            "Optional Match (ot)-[r:HAS_LEVEL]->(level:Level{deleted:false}) with ot,bt,case when r is null then [] else collect({id:id(level),name:level.name}) end as levels\n" +
            "OPTIONAL MATCH (ot)-[:HAS_SUB_TYPE]->(ost:OrganizationType{isEnable:true}) with {children: case when ost is NULL then [] else  collect({name:ost.name,id:id(ost)}) end,name:ot.name,id:id(ot),levels:levels} as orgTypes,bt\n" +
            "return collect(orgTypes) as organizationTypes,bt as businessTypes")
    OrganizationCreationData getOrganizationCreationData(long countryId);


    @Query("Match (root:Organization) where id(root)={0} with root " +
            "Match (root)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) where id(user)={1} with employment " +
            "Match (employment)-[:HAS_UNIT_PERMISSIONS]->(unitEmployment:UnitEmployment)-[:APPLICABLE_IN_UNIT]->(unit:Organization) where id(unit)={2} with unitEmployment " +
            "MATCH (unitEmployment)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) with accessPermission " +
            "Match (accessPermission)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage:AccessPage{moduleId:{3}}) with collect(r.isRead) as read " +
            "RETURN " +
            "CASE true IN read " +
            "WHEN true " +
            "THEN true " +
            "ELSE false end as result")
    boolean validateAccessGroupInUnit(long rootOrganizationId, long userId, long childOrganizationId, String accessPageId);

    @Query("MATCH (org:Organization) where id(org)={0} with org " +
            "MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p),oneTimeSync:ps.p.isOneTimeSyncPerformed,autoGenerated:ps.p.isAutoGeneratedPerformed},child:{name:ps.c.name,id:id(ps.c),oneTimeSync:ps.c.isOneTimeSyncPerformed,autoGenerated:ps.c.isAutoGeneratedPerformed}} as data")
    List<Map<String, Object>> getParentOrganization(long parentId);

    @Query("MATCH (org:Organization) where id(org)={0} with org " +
            "MATCH path=(org)-[:HAS_SUB_ORGANIZATION]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p)},child:{name:ps.c.name,id:id(ps.c)}} as data")
    List<Map<String, Object>> getSubOrgHierarchy(long organizationId);

    @Query("MATCH (c:Client{healthStatus:'ALIVE'})-[r:GET_SERVICE_FROM]-(o:Organization) where id(o)= {0}  with c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus)  with cs,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r,cs\n" +
            "OPTIONAL MATCH (ca)-[:ZIP_CODE]->(zipCode:ZipCode) with ca,c,r,zipCode,cs \n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail)  with cd,ca,c,r,zipCode,cs\n" +
            "return {name:c.firstName+\" \"+c.lastName,id:id(c),age:c.age,joiningDate:r.joinDate,gender:c.gender,emailId:cd.privateEmail,  citizenDead:c.citizenDead ,  civilianStatus:cs.name,contactNo:cd.privatePhone,emergencyNo:cd.emergencyPhone,city:zipCode.name,address:ca.street1+\", \"+ca.houseNumber,zipcode:zipCode.zipCode } as Client ORDER BY c.firstName")
    List<Map<String, Object>> getAllClientsOfOrganization(long organizationId);


    @Query("Match (cityLevel:Organization),(regionLevel:Organization{organizationLevel:'REGION'}) where id(cityLevel)={0} " +
            "CREATE UNIQUE (regionLevel)-[r:" + HAS_SUB_ORGANIZATION + "]->(cityLevel) return count(r)")
    int linkWithRegionLevelOrganization(long organizationId);

    @Query("Match (o:Organization {isEnable:true})-[:HAS_PUBLIC_PHONE_NUMBER]-> (p:PublicPhoneNumber) where p.phoneNumber={0} return o")
    Organization findOrganizationByPublicPhoneNumber(String phoneNumber);


    @Query("Match (organization:Organization) where id(organization)={0} " +
            "RETURN  " +
            "CASE 'COUNTRY' " +
            "when organization.organizationLevel " +
            "then true " +
            "else false end as data")
    boolean isThisKairosHub(long organizationId);


    @Query("MATCH (org:Organization) where id(org)={0} with org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->()-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],preKairos:ps.p.isPrekairos,kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],preKairos:ps.c.isPrekairos,kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data\n" +
            "UNION\n" +
            "MATCH (org:Organization) where id(org)={0} with org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->()-[:HAS_GROUP]->(group:Group) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data\n" +
            "UNION\n" +
            "MATCH (org:Organization) where id(org)={0} with org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data \n" +
            "UNION\n" +
            "MATCH (org:Organization) where id(org)={0} with org MATCH path=(org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps return {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data")
    List<Map<String, Object>> getOrganizationHierarchy(long parentOrganizationId);


    @Query("MATCH (parentO:Organization)-[rel]-(childO:Organization) where id(parentO)={0} AND id(childO)={1} DELETE rel")
    void deleteChildRelationOrganizationById(long parentOrganizationId, long childOrganizationId);

    @Query("MATCH (childO:Organization) where id(childO)={0} DELETE childO")
    void deleteOrganizationById(long childOrganizationId);

    @Query("Match (child:Organization) where id(child)={0} " +
            "match (child)<-[:HAS_SUB_ORGANIZATION*]-(n:Organization{organizationLevel:'CITY'}) return n limit 1")
    Organization getParentOrganizationOfCityLevel(long unitId);


    @Query("Match (o:Organization)-[rel:" + ORGANISATION_HAS_SKILL + "]->(s:Skill) where id(o)={0}  AND  id(s)={1} \n" +
            "Optional Match (o)-[:" + HAS_SUB_ORGANIZATION + "]->(sub:Organization)-[subRel:" + ORGANISATION_HAS_SKILL + "]->(s) SET rel.isEnabled=false, " +
            "rel.lastModificationDate={2},subRel.isEnabled=false,subRel.lastModificationDate={2} ")
    void removeSkillFromOrganization(long unitId, long skillId, long lastModificationDate);

    @Query("Match (country:Country) where id(country)={0} with country   optional Match (ownershipTypes:OwnershipType{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(ownershipTypes),name:ownershipTypes.name}) as ownerships,country   optional Match (businessTypes:BusinessType{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(businessTypes),name:businessTypes.name}) as businessTypes,ownerships,country   optional Match (industryTypes:IndustryType{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(industryTypes),name:industryTypes.name}) as industryTypes,ownerships,businessTypes,country optional Match (employeeLimits:EmployeeLimit{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(employeeLimits),min:employeeLimits.minimum,max:employeeLimits.maximum}) as employeeLimits,industryTypes,ownerships,businessTypes,country   optional Match (vatTypes:VatType{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(vatTypes),name:vatTypes.name,percentage:vatTypes.percentage}) as vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country   optional Match (kairosStatus:KairosStatus{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(kairosStatus),name:kairosStatus.name}) as kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country   optional Match (contractTypes:ContractType{isEnabled:true})-[:BELONGS_TO]->(country) with collect({id:id(contractTypes),name:contractTypes.name}) as contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country  \n" +
            "optional Match (country)<-[:BELONGS_TO]-(organizationType:OrganizationType{isEnable:true}) with organizationType,contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes \n" +
            "optional match (organizationType)-[r:HAS_SUB_TYPE]->(subType:OrganizationType{isEnable:true}) with distinct {id:id(organizationType),name:organizationType.name,children:case when r is null then [] else  collect({id:id(subType),name:subType.name}) end} as organizationTypes,contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes \n" +
            "return {ownershipTypes:case when ownerships[0].id is null then [] else ownerships end,industryTypes:case when industryTypes[0].id is null then [] else industryTypes end,employeeLimits:case when employeeLimits[0].id is null then [] else employeeLimits end,vatTypes:case when vatTypes[0].id is null then [] else vatTypes end,contractTypes:case when contractTypes[0].id is null then [] else contractTypes end,organizationTypes:case when organizationTypes is null then [] else collect(organizationTypes) end,kairosStatusList:case when kairosStatus[0].id is null then [] else kairosStatus end,businessTypes:case when businessTypes[0].id is null then [] else businessTypes end} as data")
    List<Map<String, Object>> getGeneralTabMetaData(long countryId);

    @Query("Match (n:Organization) where id(n)={0}\n" +
            "Match (n)-[:" + TYPE_OF + "]->(orgType:OrganizationType) with collect(distinct id(orgType)) as orgId,n\n" +
            "optional match (n)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with collect(distinct id(subType)) as subTypeId,orgId,n\n" +
            "optional match (n)-[:" + BUSINESS_TYPE + "]->(businessType:BusinessType) with subTypeId,orgId,n,collect(distinct id(businessType)) as businessTypeId\n" +
            "match (n)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) with subTypeId,orgId,n,businessTypeId,municipality\n" +
            "optional match (n)-[:" + INDUSTRY_TYPE + "]->(industryType:IndustryType) with orgId,subTypeId,businessTypeId,industryType,n,municipality\n" +
            "optional match  (n)-[:" + EMPLOYEE_LIMIT + "]->(employeeLimit:EmployeeLimit) with orgId,subTypeId,businessTypeId,employeeLimit,n,municipality,industryType\n" +
            "optional match (n)-[:" + VAT_TYPE + "]->(vatType:VatType) with orgId,subTypeId,businessTypeId,employeeLimit,vatType,n,municipality,industryType\n" +
            "optional match (n)-[:" + CONTRACT_TYPE + "]->(contractType:ContractType) with orgId,subTypeId,businessTypeId,employeeLimit,vatType,n,municipality,industryType,contractType\n" +
            "optional match (n)-[:" + KAIROS_STATUS + "]->(kairosStatus:KairosStatus) with orgId,subTypeId,businessTypeId,employeeLimit,vatType,kairosStatus,n,municipality,industryType,contractType\n" +
            "optional match (n)-[:" + OWNERSHIP_TYPE + "]->(ownershipType:OwnershipType) with orgId,subTypeId,businessTypeId,employeeLimit,vatType,kairosStatus,ownershipType,n,municipality,industryType,contractType\n" +
            "optional match (n)-[:" + HAS_LEVEL + "]-(level:Level)\n" +
            "return {name:n.name,shortName:n.shortName,eanNumber:n.eanNumber,costCenterCode:n.costCenterCode,costCenterName:n.costCenterName,industryTypeId:id(industryType),businessTypeId:businessTypeId,websiteUrl:n.webSiteUrl," +
            "employeeLimitId:id(employeeLimit),description:n.description,vatTypeId:id(vatType),ownershipTypeId:id(ownershipType),organizationTypeId:orgId,organizationSubTypeId:subTypeId,cvrNumber:n.cvrNumber,pNumber:n.pNumber," +
            "contractTypeId:id(contractType),isKairosHub:n.isKairosHub,clientSince:n.clientSince,kairosStatusId:id(kairosStatus),municipalityId:id(municipality),externalId:n.externalId,percentageWorkDeduction:n.endTimeDeduction," +
            "kmdExternalId:n.kmdExternalId, dayShiftTimeDeduction:n.dayShiftTimeDeduction, nightShiftTimeDeduction:n.nightShiftTimeDeduction,level:level.name} as data")
    Map<String, Object> getGeneralTabInfo(long organizationId);

    @Query("MATCH (c:Client)-[:GET_SERVICE_FROM]->(o:Organization) where id(c)={0} return { name:o.name , id: id(o), description: o.description } as result")
    List<Map<String, Object>> getClientServingOrganizations(Client client);


    @Query("Match(parentOrg:Organization) where id(parentOrg)= {0} with parentOrg Match(parentOrg)-[:" + HAS_SUB_ORGANIZATION + "]->(childOrg:Organization) return childOrg")
    List<Organization> getChildOrganizations(long parentOrganization);

    /* @Query("Match (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} \n" +
             "Match (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,unit,organization \n" +
             "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[r:PROVIDE_SERVICE{isEnabled:true}]-(organization) with distinct os,r \n" +
             "match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) with {children: case when os is NULL then [] else collect({id:id(os),name:os.name, customName:r.customName,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as availableServices return {availableServices:collect(availableServices)} as data \n" +
             "UNION \n" +
             "Match (organization:Organization),(unit:Organization) where id(unit)={1} and id(organization)={0}\n" +
             "Match (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,unit,organization\n" +
             "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[:PROVIDE_SERVICE{isEnabled:true}]-(organization) with distinct os,unit\n" +
             "Match (unit)-[r:PROVIDE_SERVICE{isEnabled:true}]->(os) with distinct os,r,unit\n" +
             "match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,customName:r.customName,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as selectedServices return {selectedServices:collect(selectedServices)} as data")
     */
    @Query("Match (organization:Organization),(unit:Organization) where id(organization)={0} AND id(unit)={1} \n" +
            "Match (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,unit,organization \n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[r:PROVIDE_SERVICE{isEnabled:true}]-(organization) with distinct os,r,unit \n" +
            "match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os)  with os,r,unit ,organizationService\n" +
            "OPTIONAL MATCH (unit)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "with {children: case when os is NULL then [] else collect({id:id(os),name:os.name, customName:r.customName,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name, \n" +
            "customName: CASE WHEN orgServiceCustomNameRelation IS NULL THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END\n" +
            ", description:organizationService.description} as availableServices return {availableServices:collect(availableServices)} as data \n" +
            "UNION \n" +
            "Match (organization:Organization),(unit:Organization) where id(unit)={1} and id(organization)={0}\n" +
            "Match (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,unit,organization\n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[:PROVIDE_SERVICE{isEnabled:true}]-(organization) with distinct os,unit, organization\n" +
            "Match (unit)-[r:PROVIDE_SERVICE{isEnabled:true}]->(os) with distinct os,r,unit, organization\n" +
            "match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) with os,r,unit, organizationService, organization\n" +
            "OPTIONAL MATCH (unit)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,\n" +
            "customName:CASE WHEN r.customName IS null THEN os.name ELSE r.customName END,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,\n" +
            "customName: CASE WHEN orgServiceCustomNameRelation IS NULL THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END,\n" +
            "description:organizationService.description} as selectedServices return {selectedServices:collect(selectedServices)} as data")
    List<Map<String, Object>> getServicesForUnit(long organizationId, long unitId);

    /*@Query("Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:" + SUB_TYPE_OF + "]->(organizationType:OrganizationType{isEnable:true}) with organizationType,organization MATCH (organizationType)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]-(os:OrganizationService{isEnabled:true}) with os,organization match (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os) with {children: case when os is NULL then [] else collect(distinct {id:id(os),name:os.name,description:os.description}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as availableServices return {availableServices:collect(availableServices)} as data\n" +
            "UNION\n" +
            "Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:" + SUB_TYPE_OF + "]->(organizationType:OrganizationType{isEnable:true}) with organizationType,organization \n" +
            "MATCH (organizationType)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]-(os:OrganizationService{isEnabled:true}) with distinct os,organization \n" +
            "Match (organization)-[r:" + PROVIDE_SERVICE + "{isEnabled:true}]->(os) with os, r match (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os) with {children: case when os is NULL then [] else collect({id:id(os),name:r.customName,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as selectedServices return {selectedServices:collect(selectedServices)} as data")
    */
    @Query("Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,organization MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true}) with os,organization match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) \n" +
            "with {children: case when os is NULL then [] else collect(distinct {id:id(os),name:os.name,description:os.description}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as availableServices return {availableServices:collect(availableServices)} as data\n" +
            "UNION\n" +
            "Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) with organizationType,organization \n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true}) with distinct os,organization \n" +
            "Match (organization)-[r:PROVIDE_SERVICE{isEnabled:true}]->(os) with os, r, organization match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) with os, r, organization, organizationService\n" +
            "OPTIONAL MATCH (organization)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,\n" +
            "customName:CASE WHEN r.customName IS null THEN os.name ELSE r.customName END,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description,\n" +
            "customName:CASE WHEN orgServiceCustomNameRelation IS null THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END} as selectedServices return {selectedServices:collect(selectedServices)} as data")
    List<Map<String, Object>> getServicesForParent(long organizationId);

    @Query("Match (unit:Organization),(skill:Skill) where id (unit)={0} AND id(skill) IN {1} create (unit)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:skill.name}]->(skill) return skill")
    void addSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("Match (unit:Organization),(skill:Skill) where id (unit)={0} AND id(skill) IN {1} Match (unit)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) set r.creationDate={2},r.lastModificationDate={3},r.isEnabled=true return skill")
    void updateSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("Match (unit:Organization),(organizationService:OrganizationService) where id(unit)={0} AND id(organizationService) IN {1} create unique (unit)-[r:" + PROVIDE_SERVICE + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:organizationService.name}]->(organizationService) return r")
    void addOrganizationServiceInUnit(long unitId, List<Long> organizationServiceId, long creationDate, long lastModificationDate);

    @Query("Match (o:Organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(os:Skill) where id(o)={0} AND id(os)={1} return count(r) as countOfRel")
    int isSkillAlreadyExist(long unitId, long serviceId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:" + HAS_BILLING_ADDRESS + "]->(billingAddress:ContactAddress) with billingAddress \n" +
            "optional match (billingAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) with zipCode,billingAddress\n" +
            "optional match (billingAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) with municipality,zipCode,billingAddress\n" +
            "optional match (billingAddress)-[:" + PAYMENT_TYPE + "]->(paymentType:PaymentType) with paymentType,zipCode,billingAddress,municipality \n" +
            "OPTIONAL MATCH (billingAddress)-[:" + CURRENCY + "]->(currency:Currency) with currency,paymentType,zipCode,billingAddress,municipality\n" +
            "return {id:id(billingAddress),houseNumber:billingAddress.houseNumber,floorNumber:billingAddress.floorNumber,street1:billingAddress.street1,zipCodeId:id(zipCode),city:billingAddress.city,municipalityId:id(municipality),regionName:billingAddress.regionName,province:billingAddress.province,country:billingAddress.country,latitude:billingAddress.latitude,longitude:billingAddress.longitude,paymentTypeId:id(paymentType),currencyId:id(currency),streetUrl:billingAddress.streetUrl,billingPerson:billingAddress.contactPersonForBillingAddress} as data")
    Map<String, Object> getBillingAddress(long unitId);


    @Query("match (organization:Organization)-[:HAS_GROUP*1..3]->(group:Group) where id(group)={0} MATCH (organization)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress) MATCH (contactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) MATCH (contactAddress)-[:MUNICIPALITY]->(municipality:Municipality) return organization,contactAddress,zipCode,municipality")
    OrganizationContactAddress getOrganizationByGroupId(long groupId);

    @Query("match (organization:Organization)-[:" + HAS_GROUP + "]->(group:Group)-[:" + HAS_TEAM + "]->(team:Team) where id(team)={0} return organization")
    Organization getOrganizationByTeamId(long groupId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization Match (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) with contactAddress \n" +
            "Match (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) with zipCode,contactAddress \n" +
            "optional Match (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) with municipality,zipCode,contactAddress\n" +
            "return municipality as municipality,contactAddress as contactAddress,zipCode as zipCode")
    OrganizationContactAddress getContactAddressOfOrg(long unitId);

    @Query("Match (unit:Organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) where id(unit)={0} with subType,unit\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "{isEnabled:true}]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "{isEnabled:true}]->(skill:Skill) with distinct skill,unit\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=unit.showCountryTags with DISTINCT  skill,unit,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + ORGANIZATION_HAS_TAG + "]-(unit) with  skill,unit,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "Match (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) with skill,r,otags,ctags\n" +
            "Match (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with skillCategory,skill,r,otags,ctags\n" +
            "optional match (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) where id(staff) IN {1}\n" +
            "with {staff:case when staffSkillRel is null then [] else collect(id(staff)) end} as staff,skillCategory,skill,r,otags,ctags\n" +
            "return {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:collect({id:id(skill),name:case when r is null then skill.name else r.customName end,description:skill.description,isSelected:case when r is null then false else true end, customName:case when r is null then skill.name else r.customName end, isEdited:true,staff:staff.staff,tags:ctags+otags})} as data")
    List<Map<String, Object>> getAssignedSkillsOfStaffByOrganization(long unitId, List<Long> staffId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with subType,organization\n" +
            "Match (subType)-[:" + ORG_TYPE_HAS_EXPERTISE + "]->(expertise:Expertise)-[r:" + EXPERTISE_HAS_SKILLS + "]->(skill:Skill) with skill,organization\n" +
            "create unique (organization)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{1},lastModificationDate:{2},isEnabled:true}]->(skill)")
    void assignDefaultSkillsToOrg(long orgId, long creationDate, long lastModificationDate);

    @Query("Match (n:Organization) where id(n)={0}\n" +
            "Match (n)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with subType,n\n" +
            "Match (subType)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(organizationService:OrganizationService) with organizationService,n\n" +
            "create unique (n)-[:" + PROVIDE_SERVICE + "{isEnabled:true,creationDate:{1},lastModificationDate:{2}}]->(organizationService) ")
    void assignDefaultServicesToOrg(long orgId, long creationDate, long lastModificationDate);

    /**
     * this provides tree structure of skills
     *
     * @param unitId
     * @return
     */
    /*@Query("Match (organization)-[r:"+ORGANISATION_HAS_SKILL+"{isEnabled:true}]->(skill) where id(organization)={0} with skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:"+HAS_CATEGORY+"]->(skillCategory:SkillCategory{isEnabled:true}) with skillCategory,skill,r\n" +
            "return {id:id(skillCategory),name:skillCategory.name,skills:collect({skillId:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,parentId:id(skillCategory)})} as data")*/
    /*@Query("Match (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) where id(organization)={0} with skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with skill,r,skillCategory\n" +
            " return {id:id(skillCategory),name:skillCategory.name,skills:collect({skillId:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true})} as data")*/
    @Query("Match (unit:Organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) where id(unit)={0} with skill,unit,r\n" +
            "Match (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) with  unit,skill,skillCategory,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=unit.showCountryTags with  unit,skill,skillCategory,r,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(unit) WITH skill,skillCategory,r,ctags,CASE WHEN tag IS NULL THEN [] ELSE collect({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "return {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,skills:collect(distinct {id:id(skill),name:r.customName,visitourId:skill.visitourId,description:skill.description,customName:r.customName,isEdited:true, tags:ctags+otags})} as data")
    List<Map<String, Object>> getSkillsOfOrganization(long unitId);

    Organization findByKmdExternalId(String kmdExternalId);

    @Query("MATCH (org:Organization) where id(org)={0} with org\n" +
            "optional match (org)-[:" + HAS_SUB_ORGANIZATION + "*]->(unit:Organization) with org+[unit] as coll\n" +
            "unwind coll as units with distinct units\n" +
            "return units")
    List<Organization> getUnitsWithBasicInfo(long organizationId);

    @Query("MATCH (org:Organization)-[r:" + HAS_SUB_ORGANIZATION + "]->(unit:Organization) where id(org)={0} AND id(unit)={1} return count(r)")
    Integer checkParentChildRelation(Long organizationId, Long unitId);

    @Query("Match (n:Organization{isEnable:true})-[:SUB_TYPE_OF]->(organizationType:OrganizationType) where id(organizationType)={0} return n")
    List<Organization> getOrganizationsByOrganizationType(long orgTypeId);


    @Query("match (n:Organization) where id(n)={0} with n \n" +
            "match (n)<-[:HAS_SUB_ORGANIZATION*]-(org:Organization{isParentOrganization:true})  where org.isKairosHub =false \n" +

            "match (org)-[:" + HAS_POSITION_CODE + "]->(p:PositionCode {deleted:false}) return p")
    List<PositionCode> getPositionCodesOfParentOrganization(Long organizationId);


    @Query("MATCH (o:Organization {isEnable:true} )-[:" + HAS_POSITION_CODE + "]->(p:PositionCode {deleted:false}) where id(o)={0} return p")
    List<PositionCode> getPositionCodes(Long organizationId);

    @Query(" Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[r:PROVIDE_SERVICE]->(os) where os.imported=true with distinct os,r\n" +
            "Match (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os)  with r,os,organizationService\n" +
            "Optional MATCH (organizationService)<-[:" + LINK_WITH_EXTERNAL_SERVICE + "]-(ms:OrganizationService)  with r,ms,os,organizationService \n" +
            "Optional MATCH (os)<-[:" + LINK_WITH_EXTERNAL_SERVICE + "]-(mss:OrganizationService)  with r,mss,ms,os,organizationService \n" +
            "with {children: case when os is NULL then [] else collect({id:id(os),name:os.name,description:os.description," +
            "isEnabled:r.isEnabled,created:r.creationDate,referenceId:id(mss)}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description,referenceId:id(ms)} as selectedServices return {selectedServices:collect(selectedServices)} as data")
    List<Map<String, Object>> getImportedServicesForUnit(long organizationId);

//    @Query("Match (country:Country) where id(country)={0} with country\n" +
//            "MATCH (bt:BusinessType{isEnabled:true})-[:"+BELONGS_TO+"]->(country) with collect(bt) as bt,country\n" +
//            "optional Match (country)-[:"+HAS_LEVEL+"]->(level:Level{isEnabled:true}) with collect(level) as level,bt,country\n" +
//            "MATCH (ot:OrganizationType{isEnable:true})-[:"+BELONGS_TO+"]->(country) WITH ot,bt,level\n" +
//            "OPTIONAL MATCH (ot)-[:"+HAS_SUB_TYPE+"]->(ost:OrganizationType{isEnable:true}) with {children: case when ost is NULL then [] else  collect({name:ost.name,id:id(ost)}) end,name:ot.name,id:id(ot)} as orgTypes,bt,level\n" +
//            "return collect(orgTypes) as organizationTypes,bt as businessTypes,level as levels")
//    OrganizationCreationData getOrganizationCreationData(long countryId);countryId

    @Query("Match (org:Organization)-[:" + HAS_LEVEL + "]->(level:Level) where id(org)={0} return level")
    Level getLevelOfOrganization(long organizationId);

    @Query("match (org:OrganizationType{isEnable:true}) where id(org) in {0} \n" +
            "return count(org) as matched ")
    Long findAllOrgCountMatchedByIds(List<Long> Ids);


    @Query("MATCH (o:Organization) return id(o)")
    List<Long> findAllOrganizationIds();

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:" + TYPE_OF + "]->(organizationType:OrganizationType) with organizationType,organization\n" +
            "optional match (organizationType)-[:" + HAS_SUB_TYPE + "]->(subType:OrganizationType)<-[:" + SUB_TYPE_OF + "]-(organization) with subType,organizationType,organization\n" +
            "return collect(id(organizationType)) as organizationTypes, collect(id(subType)) as organizationSubTypes")
    OrganizationTypeAndSubTypeDTO getorganizationTypeAndSubTypes(Long unitId);

    @Query("MATCH (c:Client{imported:true})-[r:GET_SERVICE_FROM]->(o:Organization) where id(o)= {0}  case when c is NULL then false else true ")
    Boolean isOrganizationHasExternalReference(Long organizationId);

    @Query("MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) where id(o)={0}  return id(country) ")
    Long getCountryId(Long organizationId);

    @Query("match(organization:Organization) where not exists (organization.phaseGenerated) OR organization.phaseGenerated=false\n" +
            "return id(organization)")
    List<Long> getAllOrganizationWithoutPhases();

    @Query("match(organization:Organization) where Id(organization) IN {0}\n" +
            "set organization.phaseGenerated=true")
    void updateOrganizationWithoutPhases(List<Long> organizationIds);


    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {0} AND ( c.firstName=~{2} OR c.lastName=~{2} ) AND c.cprNumber STARTS WITH {3} with c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {4} with cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE cs.name=~{5} with cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag)  with lat,cs,cd,ca,c,r\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName ASC SKIP {6} LIMIT 20 ")
    List<Map<String, Object>> getClientsOfOrganizationExcludeDeadWithFilterParameters(Long organizationId, String serverImageUrl, String filterByName, String filterByCPR, String filterByPhone, String filterByCivilianStatus, Integer skip);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {0} AND id(c) in {7} AND ( c.firstName=~{2} OR c.lastName=~{2} ) AND c.cprNumber STARTS WITH {3} with c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {4} with cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE cs.name=~{5} with cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) with lat,cs,cd,ca,c,r\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName ASC SKIP {6} LIMIT 20 ")
    List<Map<String, Object>> getClientsWithFilterParameters(Long organizationId, String serverImageUrl, String filterByName, String filterByCPR, String filterByPhone, String filterByCivilianStatus, Integer skip, List<Long> citizenIds);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {0} AND id(c) in {7} AND ( c.firstName=~{2} OR c.lastName=~{2} ) AND c.cprNumber STARTS WITH {3} with c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {4} with cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE cs.name=~{5} with cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) in {8} with lat,cs,cd,ca,c,r\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName ASC SKIP {6} LIMIT 20 ")
    List<Map<String, Object>> getClientsWithFilterParametersAndLatLng(Long organizationId, String serverImageUrl, String filterByName, String filterByCPR, String filterByPhone, String filterByCivilianStatus, Integer skip, List<Long> citizenIds, List<Long> latLngIds);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {0} AND ( c.firstName=~{2} OR c.lastName=~{2} ) AND c.cprNumber STARTS WITH {3} with c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  with ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail) WHERE cd.privatePhone STARTS WITH {4} with cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WHERE cs.name=~{5} with cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) in {7} with lat,cs,cd,ca,c,r\n" +
            "return {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName ASC SKIP {6} LIMIT 20 ")
    List<Map<String, Object>> getClientsOfOrganizationExcludeDeadWithFilterParametersAndLatLng(Long organizationId, String serverImageUrl, String filterByName, String filterByCPR, String filterByPhone, String filterByCivilianStatus, Integer skip, List<Long> latLngIds);

    @Query("MATCH (n:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND et.deleted={1}\n" +
            "return id(et) as id, et.name as name, et.description as description, \n" +
            "et.allowedForContactPerson as allowedForContactPerson, et.allowedForShiftPlan as allowedForShiftPlan, et.allowedForFlexPool as allowedForFlexPool ORDER BY et.name ASC")
    List<Map<String, Object>> getEmploymentTypeByOrganization(Long organizationId, Boolean isDeleted);


    @Query("MATCH (n:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2}\n" +
            "return et ORDER BY et.name ASC")
//    id(et) as id, et.name as name, et.description as description
    EmploymentType getEmploymentTypeByOrganizationAndEmploymentId(Long organizationId, Long employmentId, Boolean isDeleted);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} WITH organizationService\n" +
            "MATCH (org:Organization) WHERE id(org)={1} WITH org, organizationService\n" +
            "CREATE UNIQUE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(organizationService) SET r.customName=organizationService.name return true")
    Boolean addCustomNameOfServiceForOrganization(Long subServiceId, Long organizationId);

    //    @Query("MATCH (o:Organization)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os:OrganizationService) WHERE id(os)={0} AND id(o) ={1} SET r.customName={2} return os")
    @Query("Match (org:Organization),(os:OrganizationService) WHERE  id(org)={1} AND id(os)={0} WITH org,os\n" +
            "MERGE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os) \n" +
            "ON CREATE SET r.customName={2}\n" +
            "ON MATCH SET r.customName={2}\n" +
            " return id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfServiceForOrganization(Long serviceId, Long organizationId, String customName);

    @Query("MATCH (o:Organization)-[r:" + PROVIDE_SERVICE + "{isEnabled:true}]->(os:OrganizationService) WHERE id(os)={0} AND id(o) ={1} SET r.customName={2}\n" +
            "return id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfSubServiceForOrganization(Long serviceId, Long organizationId, String customName);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} \n" +
            "return organizationService")
    OrganizationService getServiceOfSubService(Long subServiceId);

    @Query("MATCH (organization:Organization) - [r:BELONGS_TO] -> (country:Country)\n" +
            "WHERE id(organization)={0}\n" +
            "return country")
    Country getCountry(Long organizationId);

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:true})-[:" + BELONGS_TO + "]->(c:Country)  where id(c)={0} with org\n" +
            "Match (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) with collect(id(ot)) as organizationTypeIds,org\n" +
            "optional match (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) with  collect(id(subType)) as organizationSubTypeIds,organizationTypeIds,org\n" +
            "MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) with organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) with municipality,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode\n" +
            "MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) with collect(id(businessType)) as businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality\n" +
            "optional MATCH (org)-[:" + HAS_LEVEL + "]-(level:Level{isEnabled:true}) with level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality\n" +
            "return collect({id:id(org),levelId:id(level),businessTypeIds:businessTypeIds,typeId:organizationTypeIds,subTypeId:organizationSubTypeIds,name:org.name,prekairos:org.isPrekairos,kairosHub:org.isKairosHub,description:org.description,externalId:org.externalId,homeAddress:{houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,city:contactAddress.city,zipCode:id(zipCode),regionName:contactAddress.regionName,province:contactAddress.province,municipalityName:contactAddress.municipalityName,isAddressProtected:contactAddress.isAddressProtected,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street1:contactAddress.street1,municipalityId:id(municipality)}}) as organizations")
    OrganizationQueryResult getAllUnionOfCountry(long countryId);

    @Query("match (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:true})-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) where id(subType) IN {0} " +
            "return  id(org) as id,org.name as name")
    List<UnionResponseDTO> getAllUnionsByOrganizationSubType(List<Long> organizationSubTypesId);

    @Query("Match (o:Organization)-[rel:" + ORAGANIZATION_HAS_UNIONS + "]->(union:Organization{isEnable:true,union:true}) " +
            "return  id(union) as id,union.name as name")
    List<UnionResponseDTO> getAllJoinedUnionsByOrganizationId(Long organizationId);

    @Query("Match (o:Organization)-[rel:" + ORAGANIZATION_HAS_UNIONS + "]->(union:Organization) where id(o)={0}  AND  id(union)={1} \n" +
            "SET rel.disabled=true, rel.dateOfSeparation={2} return rel")
    void removeUnionFromOrganization(long unitId, long unionId, long dateOfSeparation);

    @Query("Match (unit:Organization),(union:Organization) where id (unit)={0} AND id(union) = {1} merge (unit)-[r:" + ORAGANIZATION_HAS_UNIONS + "{dateOfJoining:{2},disabled:false}]->(union) return r")
    void addUnionInOrganization(long unitId, Long unionId, long dateOfJoining);

    @Query("Match (o:Organization)<-[:HAS_SUB_ORGANIZATION*]-(org:Organization{isParentOrganization:true,isKairosHub:false})-[rel:" + ORAGANIZATION_HAS_UNIONS + "]->(union:Organization{isEnable:true,union:true}) " +
            "return  id(union) as id,union.name as name")
    List<UnionResponseDTO> getAllUnionsFromParentOrganization(Long organizationId);

    @Query("Match (union:Organization{union:true,isEnable:true}) where id (union)={0}  return union")
    Organization findByIdAndUnionTrueAndIsEnableTrue(Long unionId);


    @Query("match(o:Organization)-[:" + HAS_SUB_ORGANIZATION + "*]-(s:Organization{isEnable:true,isKairosHub:false,union:false}) where id(o)={0} \n" +
            "return s.name as name ,id(s) as id")
    List<OrganizationBasicResponse> getOrganizationHierarchy(Long parentOrganizationId);

    @Query("match(o:Organization)-[:HAS_SUB_ORGANIZATION]-(parentOrganization:Organization{isEnable:true,isKairosHub:false,union:false}) where id(o)={0} \n"
            + "match(parentOrganization)-[:HAS_SUB_ORGANIZATION]-(units:Organization{isEnable:true,isKairosHub:false,union:false}) " +
            " with parentOrganization ,collect (units)  as data " +
            " return parentOrganization as parent,data as childUnits")
    OrganizationHierarchyData getChildHierarchyByChildUnit(Long childUnitId);

    // For Test Cases

    @Query("Match (org:Organization{union:false,isKairosHub:false,isEnable:true})-[:" + COUNTRY + "]-(c:Country) where id (c)={0}  return org LIMIT 1")
    Organization getOneParentUnitByCountry(Long countryId);
}


