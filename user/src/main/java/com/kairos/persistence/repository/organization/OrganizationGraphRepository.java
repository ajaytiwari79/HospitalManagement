package com.kairos.persistence.repository.organization;

import com.kairos.enums.OrganizationLevel;
import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.model.organization.group.Group;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.union.UnionDataQueryResult;
import com.kairos.persistence.model.organization.union.UnionQueryResult;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.user.counter.OrgTypeQueryResult;
import com.kairos.persistence.model.user.department.Department;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Interface for CRUD operation on Organization
 */
@Repository
public interface OrganizationGraphRepository extends Neo4jBaseRepository<Organization, Long>, CustomOrganizationGraphRepository {

    @Query("MATCH (o:Organization {isEnable:true}) RETURN {name:o.name, id:id(o)} as organization")
    List<Map<String, Object>> findAllOrganizations();

    Organization findByName(String name);


    Organization findByExternalId(String externalId);

    List<Organization> findByOrganizationLevel(OrganizationLevel organizationLevel);


    @Query("MATCH (o:Organization {isEnable:true} ),(o1:Organization) WHERE id(o)={0} AND id(o1)={1} create (o)-[:HAS_SUB_ORGANIZATION]->(o1) RETURN o1")
    Organization createChildOrganization(long parentOrganizationId, long childOrganizationId);

    @Query("MATCH(n:Organization {isEnable:true}),(d:Department),(u:User) WHERE id(n)={0} And id(d)={1} And id(u)={2}  create(d)-[:has_staff]->(u)")
    Organization addStaff(long organzationId, long departmentId, long userId);

    @Query("MATCH(d:Department {isEnable:true} ),(t:Team) WHERE id(d)={0} and id(t)in {1} create(d)-[:HAS_MANAGE]->(t)")
    Department linkDeptWithTeams(long departmentId, List<Long> childIds);

    @Query("MATCH (o:Organization)-[:HAS_GROUP]->(g:Group) WHERE id(o)={0} RETURN COLLECT({id:id(g),name:g.name}) as groups")
    List<Map<String, Object>> getGroups(long id);

    @Query("MATCH (o:Organization {isEnable:true} )-[:" + HAS_GROUP + "]->(g:Group {isEnable:true}) WHERE id(o)={0} AND id(g) = {1} RETURN g")
    Group getGroups(Long organizationId, Long groupId);

    @Query("MATCH (n:Organization) WHERE id(n)={0} WITH n " +
            "MATCH (n)<-[:HAS_SUB_ORGANIZATION*]-(org:Organization{isParentOrganization:true}) RETURN org limit 1")
    Organization getParentOfOrganization(Long organizationId);

    @Query("MATCH (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) WHERE id(organization)={0} WITH subType,organization\n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) WITH distinct skill,organization\n" +
            "MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) WITH DISTINCT skill,r, organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=true WITH  skill,r,organization,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(organization) WITH  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH\n" +
            " {id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags+otags})} as availableSkills\n" +
            " RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            " UNION\n" +
            " MATCH (unit:Organization)-[:SUB_TYPE_OF]->(subType:OrganizationType) WHERE id(unit)={1} WITH subType,unit\n" +
            " MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{isEnabled:true}]->(skill:Skill{isEnabled:true}) WITH distinct skill,unit\n" +
            " MATCH (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) WITH skill,unit,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=unit.showCountryTags WITH  skill,unit,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(unit) WITH  skill,r,unit,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            " MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:COLLECT({id:id(skill),name:skill.name,visitourId:r.visitourId, customName:r.customName, description:skill.description,isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            " RETURN {selectedSkills:COLLECT(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfChildOrganizationWithActualName(long organizationId, long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WITH DISTINCT skill,organization\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH distinct skill,skillCategory,organization\n" +
            "OPTIONAL MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) WITH\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            "RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            "UNION\n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(organization)={0} WITH subType,organization\n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WITH distinct skill,organization\n" +
            "MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) WITH skill,r\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId, customName:r.customName, isEdited:true})} as selectedSkills\n" +
            "RETURN {selectedSkills:COLLECT(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfParentOrganization(long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType)  \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{deleted:false}]->(skill:Skill) WITH DISTINCT skill,organization\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH distinct skill,skillCategory,organization \n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=true WITH  skill,skillCategory,organization,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) \n" +
            "WITH {id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags})} as availableSkills\n" +
            "RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) \n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) \n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=organization.showCountryTags WITH  skill,organization,skillCategory,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[" + ORGANIZATION_HAS_TAG + "]-(organization) WITH  skill,r,organization,skillCategory,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags WITH\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId, customName:r.customName, isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            "RETURN {selectedSkills:COLLECT(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfParentOrganizationWithActualName(long unitId);

    @Query("MATCH (g:Group)-[:" + HAS_TEAM + "]-(t:Team) WHERE id(t) ={0}  WITH g as group MATCH(group)-[:" + GROUP_HAS_SKILLS + "]->(s:Skill) " +
            " WITH s AS skill " +
            "MATCH (sc:SkillCategory)-[:" + HAS_CATEGORY + "]-(skill) RETURN  " +
            "{ id:id(sc), " +
            "  name:sc.name, " +
            "  skills:COLLECT({ " +
            "  id:id(skill), " +
            "  name:skill.name " +
            "}) " +
            "} AS skillList ")
    List<Map<String, Object>> getTeamGroupSkill(Long teamId);

    @Query("MATCH (o:Organization)-[:CONTACT_ADDRESS]->(ca:ContactAddress) WHERE id(o)={0} RETURN ca")
    ContactAddress getOrganizationAddressDetails(Long organizationId);

    @Query("MATCH (o:Organization)-[:" + HAS_GROUP + "]->(g:Group) WHERE id(g) ={0}  WITH o as org " +
            "MATCH (org)-[:" + PROVIDE_SERVICE + "]->(s:OrganizationService) WITH s " +
            "MATCH (os:OrganizationService)-[:ORGANIZATION_SUB_SERVICE]-(s)" +
            "RETURN { " +
            "id:id(os), " +
            "name:os.name, " +
            "subServices: COLLECT({ " +
            " id:id(s), " +
            " name:s.name " +
            "}) " +
            "} as serviceList ")
    List<Map<String, Object>> getGroupOrganizationServices(Long groupId);


    @Query("MATCH (g:Group)-[:" + HAS_TEAM + "]->(t:Team) WHERE id(t) ={0} WITH g as grp " +
            "MATCH (grp)-[:" + GROUP_HAS_SERVICES + "]->(s:OrganizationService)  WITH s as ss" +
            " MATCH (os:OrganizationService)-[:ORGANIZATION_SUB_SERVICE]-(ss) " +
            "RETURN { id:id(os), name:os.name, subServices:" +
            " COLLECT({  id:id(ss), " +
            " name:ss.name }) } as serviceList ")
    List<Map<String, Object>> getTeamGroupServices(Long teamId);


    // Services
    @Query("MATCH (g:Group)-[:" + GROUP_HAS_SERVICES + "]-(os:OrganizationService) WHERE id(g) = {0} WITH os as ss " +
            "MATCH (os:OrganizationService)-[:" + ORGANIZATION_SUB_SERVICE + "]-(ss) " +
            "RETURN { " +
            "id:id(os), " +
            "name:os.name, " +
            "subServices: COLLECT({ " +
            " id:id(ss), " +
            " name:ss.name " +
            "}) " +
            "} as serviceList")
    List<Map<String, Object>> getGroupAllSelectedServices(Long groupId);

    @Query("MATCH(t:Team)-[:" + TEAM_HAS_SERVICES + "]-(os:OrganizationService) WHERE id(t)={0} WITH os as ss " +
            "MATCH (os:OrganizationService)-[:" + ORGANIZATION_SUB_SERVICE + "]-(ss) " +
            " RETURN { " +
            " id:id(os), " +
            " name:os.name, " +
            " subServices: COLLECT({ " +
            " id:id(ss), " +
            " name:ss.name " +
            "}) " +
            "} as serviceList")
    List<Map<String, Object>> getTeamAllSelectedSubServices(Long teamId);

    @Query("MATCH (user:User)<-[:" + IS_A + "]-(c:Client)-[:GET_SERVICE_FROM]->(o:Organization)  WHERE id(o)= {0} WITH c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  WITH ca,c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) WITH contactDetail, ca, c,user\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(civilianStatus:CitizenStatus) WITH civilianStatus, contactDetail, ca, c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) WITH lat,  civilianStatus, contactDetail, ca, c,user\n" +
            "RETURN {name:user.firstName+\" \" +user.lastName,id:id(c) , gender:user.gender, cprNumber:user.cprNumber , healthStatus:c.healthStatus,citizenDead:c.citizenDead, phoneNumber:contactDetail.mobilePhone, clientStatus:id(civilianStatus), " +
            "address:ca.houseNumber+\" \" +ca.street1, lat:ca.latitude, lng:ca.longitude, profilePic: {1} + c.profilePic, age:user.age, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganization(Long organizationId, String imageUrl);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Organization) WHERE id(o)= {0}  WITH c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  WITH ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail)  WITH cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus) WITH cs,cd,ca,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) WITH lat,cs,cd,ca,c,r\n" +
            "RETURN {name:c.firstName+\" \" +c.lastName,id:id(c), age:c.age, emailId:c.email, profilePic: {1} + c.profilePic, gender:c.gender, cprNumber:c.cprNumber , citizenDead:c.citizenDead, joiningDate:r.joinDate,city:ca.city," +
            "address:ca.houseNumber+\" \" +ca.street1, phoneNumber:cd.privatePhone, workNumber:cd.workPhone, clientStatus:id(cs), lat:ca.latitude, lng:ca.longitude, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganizationExcludeDead(Long organizationId, String serverImageUrl);


    @Query("MATCH (c:Client{citizenDead:false}) WHERE id(c) IN {0} RETURN {name:c.firstName+\" \" +c.lastName,id:id(c) , gender:c.gender, cprNumber:c.cprNumber  , citizenDead:c.citizenDead }  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsByClintIdList(List<Long> clientIds);

    @Query("MATCH (organization:Organization)-[:HAS_SUB_ORGANIZATION]->(unit:Organization) WHERE id(organization)={0} WITH unit  " +
            "OPTIONAL MATCH (unit)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress) WITH " +
            "contactAddress,unit " +
            "MATCH (contactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) WITH zipCode,contactAddress,unit " +
            "RETURN COLLECT({id:id(unit),name:unit.name,shortName:unit.shortName,contactAddress:case when contactAddress is null then [] else {id:id(contactAddress),city:contactAddress.city,longitude:contactAddress.longitude,latitude:contactAddress.latitude, " +
            "street:contactAddress.street,zipCodeId:id(zipCode),floorNumber:contactAddress.floorNumber, " +
            "houseNumber:contactAddress.houseNumber,province:contactAddress.province,country:contactAddress.country,regionName:contactAddress.regionName, " +
            "municipalityName:contactAddress.municipalityName} end}) AS unitList")
    List<Map<String, Object>> getUnits(long organizationId);

    @Query("MATCH (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) WHERE id(o)={0} AND id(os)={1} SET r.isEnabled=false")
    void removeServiceFromOrganization(long unitId, long serviceId);

    @Query("MATCH (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) WHERE id(o)={0} AND id(os)={1} SET r.customName=os.name, r.isEnabled=true")
    void updateServiceFromOrganization(long unitId, long serviceId);

    @Query("MATCH (o:Organization)-[r:PROVIDE_SERVICE]->(os:OrganizationService) WHERE id(o)={0} AND id(os)={1} RETURN count(r) as countOfRel")
    int isServiceAlreadyExist(long unitId, long serviceId);

    @Query("MATCH (o:Organization)-[:" + ORGANIZATION_HAS_DEPARTMENT + "]->(dept:Department) WHERE id(o)={0} RETURN dept")
    List<Department> getAllDepartments(Long organizationId);


    @Query("MATCH (o:Organization)-[:" + ORGANIZATION_HAS_DEPARTMENT + "]-(d:Department) WHERE id(d)={0} RETURN id(o)")
    Long getOrganizationByDepartmentId(Long departmentId);

    @Query("MATCH (o:Organization)-[:" + HAS_SUB_ORGANIZATION + "*..4]-(co:Organization) WHERE id(o)={0}  RETURN " +
            "COLLECT ({name:co.name,id:id(co),level:co.organizationLevel}) as organizationList")
    List<Map<String, Object>> getOrganizationChildList(Long id);


    @Query("MATCH (o:Organization)-[:HAS_GROUP]-(g:Group) WHERE id(o)={0} WITH g as grp MATCH (grp)-[:HAS_TEAM]-(t:Team) RETURN { id:id(t) , name:t.name} as result")
    List<Map<String, Object>> getUnitTeams(Long unitId);

    @Query("MATCH (o:Organization {isEnable:true})-[:HAS_SETTING]-(os:OrganizationSetting) WHERE id(o)={0} WITH os as setting MATCH (setting)-[:OPENING_HOUR]-(oh:OpeningHours) RETURN oh order by oh.index")
    List<OpeningHours> getOpeningHours(Long organizationId);

    // TODO REMOVE VIPUL
    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:false})-[:" + BELONGS_TO + "]->(c:Country)  WHERE id(c)={0} WITH org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]->(companyCategory:CompanyCategory) WITH companyCategory, org\n" +
            "MATCH (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) WITH COLLECT(id(ot)) as organizationTypeIds,org,companyCategory\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH  COLLECT(id(subType)) as organizationSubTypeIds,organizationTypeIds,org,companyCategory\n" +
            "MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) WITH organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,companyCategory\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,companyCategory\n" +
            "MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_LEVEL + "]-(level:Level{isEnabled:true}) WITH level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory  ORDER BY org.name\n" +
            "OPTIONAL MATCH (emp:Employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org)\n" +
            "OPTIONAL MATCH (unitPermission)-[r1:" + HAS_ACCESS_GROUP + "]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'})\n" +
            "OPTIONAL MATCH (emp)-[:" + BELONGS_TO + "]-(staff:Staff)-[:" + BELONGS_TO + "]-(u:User)\n" +
            "WITH COLLECT(u) as unitManagers, COLLECT(ag) as accessGroups,level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "WITH CASE WHEN size(unitManagers)>0 THEN unitManagers[0] ELSE null END as unitManager,\n" +
            "CASE WHEN size(accessGroups)>0 THEN accessGroups[0] ELSE null END as accessGroup,\n" +
            "level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "RETURN COLLECT({unitManager:CASE WHEN unitManager IS NULL THEN null ELSE  {id:id(unitManager), email:unitManager.email, firstName:unitManager.firstName, lastName:unitManager.lastName, cprNumber:unitManager.cprNumber, accessGroupId:id(accessGroup), accessGroupName:accessGroup.name} END ,id:id(org),levelId:id(level),companyCategoryId:id(companyCategory),businessTypeIds:businessTypeIds,typeId:organizationTypeIds,subTypeId:organizationSubTypeIds,name:org.name,prekairos:org.isPrekairos,kairosHub:org.isKairosHub,description:org.description,externalId:org.externalId,boardingCompleted:org.boardingCompleted,desiredUrl:org.desiredUrl,shortCompanyName:org.shortCompanyName,kairosCompanyId:org.kairosCompanyId,companyType:org.companyType,vatId:org.vatId,costCenter:org.costCenter,costCenterId:org.costCenterId,companyUnitType:org.companyUnitType,contactAddress:{houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,city:contactAddress.city,zipCodeId:id(zipCode),regionName:contactAddress.regionName,province:contactAddress.province,municipalityName:contactAddress.municipalityName,isAddressProtected:contactAddress.isAddressProtected,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street1:contactAddress.street1,municipalityId:id(municipality)}}) as organizations")
    OrganizationQueryResult getParentOrganizationOfRegion(long countryId);


    //name             boardingCompleted     typeId             subTypeId     accountTYpe             zipCodeId

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:false})-[:" + BELONGS_TO + "]->(c:Country)  WHERE id(c)={0} \n" +
            "OPTIONAL MATCH (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) WITH org,ot\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH COLLECT(id(subType)) as organizationSubTypeIds,org,ot\n" +
            "OPTIONAL MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH organizationSubTypeIds,org,ot,zipCode\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]-(accountType:AccountType)\n" +
            "RETURN id(org) as id,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,id(ot) as typeId,organizationSubTypeIds as subTypeId," +
            "id(accountType) as accountTypeId ,id(zipCode) as zipCodeId ORDER BY org.name")
    List<OrganizationBasicResponse> getAllParentOrganizationOfCountry(Long countryId);

    @Query("MATCH (organization:Organization)-[:" + HAS_SUB_ORGANIZATION + "]->(org:Organization{union:false,deleted:false}) WHERE id(organization)={0}  \n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]-(companyCategory:CompanyCategory) WITH companyCategory, org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_UNIT_TYPE + "]-(unitType:UnitType) WITH companyCategory, org,unitType\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]-(accountType:AccountType) WITH companyCategory,accountType, org,unitType\n" +
            "OPTIONAL MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,org,companyCategory,accountType,unitType\n" +
            "OPTIONAL MATCH (org)-[:" + TYPE_OF + "]-(ot:OrganizationType) WITH id(ot) as typeId,businessTypeIds,org,companyCategory,accountType,unitType\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]-(subType:OrganizationType) WITH  COLLECT(id(subType)) as subTypeIds,typeId,businessTypeIds,org,companyCategory,accountType,unitType\n" +
            "RETURN id(unitType) as unitTypeId,subTypeIds as subTypeId ,typeId as typeId ,id(org) as id,org.kairosId as kairosId,id(companyCategory) as companyCategoryId,businessTypeIds as businessTypeIds,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,org.desiredUrl as desiredUrl," +
            "id(accountType) as accountTypeId,org.shortCompanyName as shortCompanyName,org.kairosCompanyId as kairosCompanyId,org.companyType as companyType,org.vatId as vatId," +
            "org.companyUnitType as companyUnitType ORDER BY org.name ")
    List<OrganizationBasicResponse> getOrganizationGdprAndWorkCenter(Long organizationId);

    @Query("MATCH (country:Country) WHERE id(country)={0} WITH country \n" +
            "OPTIONAL MATCH (bt:BusinessType{isEnabled:true})-[:" + BELONGS_TO + "]->(country) WITH COLLECT(bt) as bt,country \n" +
            "OPTIONAL MATCH (cc:CompanyCategory{deleted:false})-[:" + BELONGS_TO + "]->(country) WITH  cc,bt,country \n" +
            "OPTIONAL MATCH (ot:OrganizationType{isEnable:true})-[:" + BELONGS_TO + "]->(country) WITH COLLECT(cc) as cc,bt,country,ot\n" +
            "OPTIONAL MATCH (ot)-[r:" + HAS_LEVEL + "]->(level:Level{deleted:false}) " +
            "  WITH ot,bt,cc,country, case when r is null then [] else COLLECT({id:id(level),name:level.name}) end as levels \n" +
            "OPTIONAL MATCH (ot)-[:" + HAS_SUB_TYPE + "]->(ost:OrganizationType{isEnable:true}) " +
            "WITH {children: case when ost is NULL then [] else COLLECT({name:ost.name,id:id(ost)}) end,name:ot.name,id:id(ot),levels:levels} as orgTypes,bt,cc,country " +
            "WITH COLLECT(orgTypes) as organizationTypes,bt,cc,country " +
            "OPTIONAL MATCH (os:OrganizationService{isEnabled:true})<-[:HAS_ORGANIZATION_SERVICES]-(country) WITH organizationTypes,bt,cc,country\n" +
            "OPTIONAL MATCH (oss)<-[:" + ORGANIZATION_SUB_SERVICE + "]-(os) " +
            " WITH {children: case when oss is NULL then [] else COLLECT({name:oss.name,id:id(oss)}) end,name:os.name,id:id(os)} as orgServices,bt,organizationTypes,cc,country " +
            " WITH COLLECT(orgServices) as serviceTypes,bt,organizationTypes,cc,country " +
            " OPTIONAL MATCH(country)<-[:" + IN_COUNTRY + "]-(accountType:AccountType{deleted:false}) WITH organizationTypes,bt ,cc ,serviceTypes,COLLECT(accountType) as accountTypes \n" +
            " OPTIONAL MATCH(country)<-[:" + IN_COUNTRY + "]-(unitType:UnitType{deleted:false}) WITH organizationTypes,bt ,cc ,serviceTypes,accountTypes,COLLECT(unitType) as unitTypes \n" +
            "RETURN organizationTypes,bt as businessTypes,cc as companyCategories,serviceTypes,accountTypes,unitTypes")
    OrganizationCreationData getOrganizationCreationData(long countryId);


    @Query("MATCH (root:Organization) WHERE id(root)={0} WITH root " +
            "MATCH (root)-[:HAS_EMPLOYMENTS]->(employment:Employment)-[:BELONGS_TO]->(staff:Staff)-[:BELONGS_TO]->(user:User) WHERE id(user)={1} WITH employment " +
            "MATCH (employment)-[:HAS_UNIT_PERMISSIONS]->(unitPermission:UnitPermission)-[:APPLICABLE_IN_UNIT]->(unit:Organization) WHERE id(unit)={2} WITH unitPermission " +
            "MATCH (unitPermission)-[:HAS_ACCESS_PERMISSION]->(accessPermission:AccessPermission)-[:HAS_ACCESS_GROUP]->(accessGroup:AccessGroup) WITH accessPermission " +
            "MATCH (accessPermission)-[r:HAS_ACCESS_PAGE_PERMISSION]->(accessPage:AccessPage{moduleId:{3}}) WITH COLLECT(r.isRead) as read " +
            "RETURN " +
            "CASE true IN read " +
            "WHEN true " +
            "THEN true " +
            "ELSE false end as result")
    boolean validateAccessGroupInUnit(long rootOrganizationId, long userId, long childOrganizationId, String accessPageId);

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org " +
            "MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p),oneTimeSync:ps.p.isOneTimeSyncPerformed,autoGenerated:ps.p.isAutoGeneratedPerformed},child:{name:ps.c.name,id:id(ps.c),oneTimeSync:ps.c.isOneTimeSyncPerformed,autoGenerated:ps.c.isAutoGeneratedPerformed}} as data")
    List<Map<String, Object>> getParentOrganization(long parentId);

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org " +
            "MATCH path=(org)-[:HAS_SUB_ORGANIZATION]->() WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p)},child:{name:ps.c.name,id:id(ps.c)}} as data")
    List<Map<String, Object>> getSubOrgHierarchy(long organizationId);

    @Query("MATCH (c:Client{healthStatus:'ALIVE'})-[r:GET_SERVICE_FROM]-(o:Organization) WHERE id(o)= {0}  WITH c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus)  WITH cs,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  WITH ca,c,r,cs\n" +
            "OPTIONAL MATCH (ca)-[:ZIP_CODE]->(zipCode:ZipCode) WITH ca,c,r,zipCode,cs \n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail)  WITH cd,ca,c,r,zipCode,cs\n" +
            "RETURN {name:c.firstName+\" \"+c.lastName,id:id(c),age:c.age,joiningDate:r.joinDate,gender:c.gender,emailId:cd.privateEmail,  citizenDead:c.citizenDead ,  civilianStatus:cs.name,contactNo:cd.privatePhone,emergencyNo:cd.emergencyPhone,city:zipCode.name,address:ca.street1+\", \"+ca.houseNumber,zipcode:zipCode.zipCode } as Client ORDER BY c.firstName")
    List<Map<String, Object>> getAllClientsOfOrganization(long organizationId);


    @Query("MATCH (cityLevel:Organization),(regionLevel:Organization{organizationLevel:'REGION'}) WHERE id(cityLevel)={0} " +
            "CREATE UNIQUE (regionLevel)-[r:" + HAS_SUB_ORGANIZATION + "]->(cityLevel) RETURN count(r)")
    int linkWithRegionLevelOrganization(long organizationId);

    @Query("MATCH (o:Organization {isEnable:true})-[:HAS_PUBLIC_PHONE_NUMBER]-> (p:PublicPhoneNumber) WHERE p.phoneNumber={0} RETURN o")
    Organization findOrganizationByPublicPhoneNumber(String phoneNumber);


    @Query("MATCH (organization:Organization) WHERE id(organization)={0} " +
            "RETURN  " +
            "CASE 'COUNTRY' " +
            "when organization.organizationLevel " +
            "then true " +
            "else false end as data")
    boolean isThisKairosHub(long organizationId);


    @Query("MATCH (org:Organization{isEnable:true,boardingCompleted: true}) where id(org)={0} WITH org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->({isEnable:true,boardingCompleted: true})-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],preKairos:ps.p.isPrekairos,kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],preKairos:ps.c.isPrekairos,kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data\n" +
            "UNION\n" +
            "MATCH (org:Organization{isEnable:true,boardingCompleted: true}) where id(org)={0} WITH org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->({isEnable:true,boardingCompleted: true})-[:HAS_GROUP]->(group:Group) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data\n" +
            "UNION\n" +
            "MATCH (org:Organization{isEnable:true,boardingCompleted: true}) where id(org)={0} WITH org MATCH path=(org)-[:HAS_SUB_ORGANIZATION*]->({isEnable:true,boardingCompleted: true}) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data \n" +
            "UNION\n" +
            "MATCH (org:Organization{isEnable:true,boardingCompleted: true}) where id(org)={0} WITH org MATCH path=(org)-[:HAS_GROUP]->(group:Group)-[:HAS_TEAM]->(team:Team) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p),type:labels(ps.p)[0],kairosHub:case when labels(ps.p)[0]='Organization' then ps.p.isKairosHub else false end,preKairos:ps.p.isPrekairos,enabled:case when labels(ps.p)[0]='Organization' then ps.p.isEnable else ps.p.isEnabled end,oneTimeSyncPerformed:ps.p.isOneTimeSyncPerformed,union:ps.p.union,autoGeneratedPerformed:ps.p.isAutoGeneratedPerformed,parentOrganization:ps.p.isParentOrganization,organizationLevel:ps.p.organizationLevel},child:{name:ps.c.name,id:id(ps.c),type:labels(ps.c)[0],kairosHub:case when labels(ps.c)[0]='Organization' then ps.c.isKairosHub else false end,preKairos:ps.c.isPrekairos,enabled:case when labels(ps.c)[0]='Organization' then ps.c.isEnable else ps.c.isEnabled end,oneTimeSyncPerformed:ps.c.isOneTimeSyncPerformed,autoGeneratedPerformed:ps.c.isAutoGeneratedPerformed,parentOrganization:ps.c.isParentOrganization,timeZone:ps.c.timeZone,union:ps.c.union,organizationLevel:ps.c.organizationLevel}} as data")
    List<Map<String, Object>> getOrganizationHierarchy(long parentOrganizationId);


    @Query("MATCH (parentO:Organization)-[rel]-(childO:Organization) WHERE id(parentO)={0} AND id(childO)={1} DELETE rel")
    void deleteChildRelationOrganizationById(long parentOrganizationId, long childOrganizationId);

    @Query("MATCH (childO:Organization) WHERE id(childO)={0} DELETE childO")
    void deleteOrganizationById(long childOrganizationId);

    @Query("MATCH (child:Organization) WHERE id(child)={0} " +
            "MATCH (child)<-[:HAS_SUB_ORGANIZATION*]-(n:Organization{organizationLevel:'CITY'}) RETURN n limit 1")
    Organization getParentOrganizationOfCityLevel(long unitId);


    @Query("MATCH (o:Organization)-[rel:" + ORGANISATION_HAS_SKILL + "]->(s:Skill) WHERE id(o)={0}  AND  id(s)={1} \n" +
            "OPTIONAL MATCH (o)-[:" + HAS_SUB_ORGANIZATION + "]->(sub:Organization)-[subRel:" + ORGANISATION_HAS_SKILL + "]->(s) SET rel.isEnabled=false, " +
            "rel.lastModificationDate={2},subRel.isEnabled=false,subRel.lastModificationDate={2} ")
    void removeSkillFromOrganization(long unitId, long skillId, long lastModificationDate);

    @Query("MATCH (country:Country) WHERE id(country)={0} WITH country   " +
            "OPTIONAL MATCH (ownershipTypes:OwnershipType{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(ownershipTypes),name:ownershipTypes.name}) as ownerships,country   " +
            "OPTIONAL MATCH (businessTypes:BusinessType{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(businessTypes),name:businessTypes.name}) as businessTypes,ownerships,country  " +
            " OPTIONAL MATCH (industryTypes:IndustryType{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(industryTypes),name:industryTypes.name}) as industryTypes,ownerships,businessTypes,country " +
            "OPTIONAL MATCH (employeeLimits:EmployeeLimit{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(employeeLimits),min:employeeLimits.minimum,max:employeeLimits.maximum}) as employeeLimits,industryTypes,ownerships,businessTypes,country  " +
            " OPTIONAL MATCH (vatTypes:VatType{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(vatTypes),name:vatTypes.name,percentage:vatTypes.percentage}) as vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country  " +
            " OPTIONAL MATCH (kairosStatus:KairosStatus{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(kairosStatus),name:kairosStatus.name}) as kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country  " +
            " OPTIONAL MATCH (contractTypes:ContractType{isEnabled:true})-[:BELONGS_TO]->(country) WITH COLLECT({id:id(contractTypes),name:contractTypes.name}) as contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes,country  \n" +
            "OPTIONAL MATCH (country)<-[:BELONGS_TO]-(organizationType:OrganizationType{isEnable:true}) WITH organizationType,contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes \n" +
            "OPTIONAL MATCH (organizationType)-[r:HAS_SUB_TYPE]->(subType:OrganizationType{isEnable:true}) WITH distinct {id:id(organizationType),name:organizationType.name,children:case when r is null then [] else  COLLECT({id:id(subType),name:subType.name}) end} as organizationTypes,contractTypes,kairosStatus,vatTypes,employeeLimits,industryTypes,ownerships,businessTypes \n" +
            "RETURN {ownershipTypes:case when ownerships[0].id is null then [] else ownerships end,industryTypes:case when industryTypes[0].id is null then [] else industryTypes end,employeeLimits:case when employeeLimits[0].id is null then [] else employeeLimits end,vatTypes:case when vatTypes[0].id is null then [] else vatTypes end,contractTypes:case when contractTypes[0].id is null then [] else contractTypes end,organizationTypes:case when organizationTypes is null then [] else COLLECT(organizationTypes) end,kairosStatusList:case when kairosStatus[0].id is null then [] else kairosStatus end,businessTypes:case when businessTypes[0].id is null then [] else businessTypes end} as data")
    List<Map<String, Object>> getGeneralTabMetaData(long countryId);

    @Query("MATCH (n:Organization) WHERE id(n)={0}\n" +
            "MATCH (n)-[:" + TYPE_OF + "]->(orgType:OrganizationType) WITH id(orgType) as orgId,n\n" +
            "OPTIONAL MATCH (n)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH COLLECT(distinct id(subType)) as subTypeId,orgId,n\n" +
            "OPTIONAL MATCH (n)-[:" + BUSINESS_TYPE + "]->(businessType:BusinessType) WITH subTypeId,orgId,n,COLLECT(distinct id(businessType)) as businessTypeId\n" +
            "MATCH (n)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH subTypeId,orgId,n,businessTypeId,municipality\n" +
            "OPTIONAL MATCH (n)-[:" + INDUSTRY_TYPE + "]->(industryType:IndustryType) WITH orgId,subTypeId,businessTypeId,industryType,n,municipality\n" +
            "OPTIONAL MATCH  (n)-[:" + EMPLOYEE_LIMIT + "]->(employeeLimit:EmployeeLimit) WITH orgId,subTypeId,businessTypeId,employeeLimit,n,municipality,industryType\n" +
            "OPTIONAL MATCH (n)-[:" + VAT_TYPE + "]->(vatType:VatType) WITH orgId,subTypeId,businessTypeId,employeeLimit,vatType,n,municipality,industryType\n" +
            "OPTIONAL MATCH (n)-[:" + CONTRACT_TYPE + "]->(contractType:ContractType) WITH orgId,subTypeId,businessTypeId,employeeLimit,vatType,n,municipality,industryType,contractType\n" +
            "OPTIONAL MATCH (n)-[:" + KAIROS_STATUS + "]->(kairosStatus:KairosStatus) WITH orgId,subTypeId,businessTypeId,employeeLimit,vatType,kairosStatus,n,municipality,industryType,contractType\n" +
            "OPTIONAL MATCH (n)-[:" + OWNERSHIP_TYPE + "]->(ownershipType:OwnershipType) WITH orgId,subTypeId,businessTypeId,employeeLimit,vatType,kairosStatus,ownershipType,n,municipality,industryType,contractType\n" +
            "OPTIONAL MATCH (n)-[:" + HAS_LEVEL + "]-(level:Level)\n" +
            "RETURN {name:n.name,shortName:n.shortName,eanNumber:n.eanNumber,costCenterCode:n.costCenterCode,costCenterName:n.costCenterName,industryTypeId:id(industryType),businessTypeId:businessTypeId,websiteUrl:n.webSiteUrl," +
            "employeeLimitId:id(employeeLimit),description:n.description,vatTypeId:id(vatType),ownershipTypeId:id(ownershipType),organizationTypeId:orgId,organizationSubTypeId:subTypeId,cvrNumber:n.cvrNumber,pNumber:n.pNumber," +
            "contractTypeId:id(contractType),isKairosHub:n.isKairosHub,clientSince:n.clientSince,kairosStatusId:id(kairosStatus),municipalityId:id(municipality),externalId:n.externalId,percentageWorkDeduction:n.endTimeDeduction," +
            "kmdExternalId:n.kmdExternalId, dayShiftTimeDeduction:n.dayShiftTimeDeduction, nightShiftTimeDeduction:n.nightShiftTimeDeduction,level:level.name} as data")
    Map<String, Object> getGeneralTabInfo(long organizationId);


    @Query("MATCH (organization:Organization),(unit:Organization) WHERE id(organization)={0} AND id(unit)={1} \n" +
            "MATCH (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) WITH organizationType,unit,organization \n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[r:PROVIDE_SERVICE{isEnabled:true}]-(organization) WITH distinct os,r,unit \n" +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os)  WITH os,r,unit ,organizationService\n" +
            "OPTIONAL MATCH (unit)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT({id:id(os),name:os.name, customName:r.customName,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name, \n" +
            "customName: CASE WHEN orgServiceCustomNameRelation IS NULL THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END\n" +
            ", description:organizationService.description} as availableServices RETURN {availableServices:COLLECT(availableServices)} as data \n" +
            "UNION \n" +
            "MATCH (organization:Organization),(unit:Organization) WHERE id(unit)={1} and id(organization)={0}\n" +
            "MATCH (unit)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) WITH organizationType,unit,organization\n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true})<-[:PROVIDE_SERVICE{isEnabled:true}]-(organization) WITH distinct os,unit, organization\n" +
            "MATCH (unit)-[r:PROVIDE_SERVICE{isEnabled:true}]->(os) WITH distinct os,r,unit, organization\n" +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) WITH os,r,unit, organizationService, organization\n" +
            "OPTIONAL MATCH (unit)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT({id:id(os),name:os.name,\n" +
            "customName:CASE WHEN r.customName IS null THEN os.name ELSE r.customName END,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,\n" +
            "customName: CASE WHEN orgServiceCustomNameRelation IS NULL THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END,\n" +
            "description:organizationService.description} as selectedServices RETURN {selectedServices:COLLECT(selectedServices)} as data")
    List<Map<String, Object>> getServicesForUnit(long organizationId, long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} WITH organization MATCH (organization)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) WITH organizationType,organization MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true}) WITH os,organization MATCH (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT(distinct {id:id(os),name:os.name,description:os.description}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as availableServices RETURN {availableServices:COLLECT(availableServices)} as data\n" +
            "UNION\n" +
            "MATCH (organization:Organization) WHERE id(organization)={0} WITH organization MATCH (organization)-[:SUB_TYPE_OF]->(organizationType:OrganizationType{isEnable:true}) WITH organizationType,organization \n" +
            "MATCH (organizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(os:OrganizationService{isEnabled:true}) WITH distinct os,organization \n" +
            "MATCH (organization)-[r:PROVIDE_SERVICE{isEnabled:true}]->(os) WITH os, r, organization MATCH (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os) WITH os, r, organization, organizationService\n" +
            "OPTIONAL MATCH (organization)-[orgServiceCustomNameRelation:HAS_CUSTOM_SERVICE_NAME_FOR]-(organizationService:OrganizationService) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT({id:id(os),name:os.name,\n" +
            "customName:CASE WHEN r.customName IS null THEN os.name ELSE r.customName END,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description,\n" +
            "customName:CASE WHEN orgServiceCustomNameRelation IS null THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END} as selectedServices RETURN {selectedServices:COLLECT(selectedServices)} as data")
    List<Map<String, Object>> getServicesForParent(long organizationId);

    @Query("MATCH (unit:Organization),(skill:Skill) WHERE id (unit)={0} AND id(skill) IN {1} create (unit)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:skill.name}]->(skill)")
    void addSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("MATCH (unit:Organization),(skill:Skill) WHERE id (unit)={0} AND id(skill) IN {1} MATCH (unit)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) set r.creationDate={2},r.lastModificationDate={3},r.isEnabled=true")
    void updateSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("MATCH (unit:Organization),(organizationService:OrganizationService) WHERE id(unit)={0} AND id(organizationService) IN {1} create unique (unit)-[r:" + PROVIDE_SERVICE + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:organizationService.name}]->(organizationService)")
    void addOrganizationServiceInUnit(long unitId, List<Long> organizationServiceId, long creationDate, long lastModificationDate);

    @Query("MATCH (o:Organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(os:Skill) WHERE id(o)={0} AND id(os)={1} RETURN count(r) as countOfRel")
    int isSkillAlreadyExist(long unitId, long serviceId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} WITH organization MATCH (organization)-[:" + HAS_BILLING_ADDRESS + "]->(billingAddress:ContactAddress) WITH billingAddress \n" +
            "OPTIONAL MATCH (billingAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,billingAddress\n" +
            "OPTIONAL MATCH (billingAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,billingAddress\n" +
            "OPTIONAL MATCH (billingAddress)-[:" + PAYMENT_TYPE + "]->(paymentType:PaymentType) WITH paymentType,zipCode,billingAddress,municipality \n" +
            "OPTIONAL MATCH (billingAddress)-[:" + CURRENCY + "]->(currency:Currency) WITH currency,paymentType,zipCode,billingAddress,municipality\n" +
            "RETURN {id:id(billingAddress),houseNumber:billingAddress.houseNumber,floorNumber:billingAddress.floorNumber,street1:billingAddress.street1,zipCodeId:id(zipCode),city:billingAddress.city,municipalityId:id(municipality),regionName:billingAddress.regionName,province:billingAddress.province,country:billingAddress.country,latitude:billingAddress.latitude,longitude:billingAddress.longitude,paymentTypeId:id(paymentType),currencyId:id(currency),streetUrl:billingAddress.streetUrl,billingPerson:billingAddress.contactPersonForBillingAddress} as data")
    Map<String, Object> getBillingAddress(long unitId);


    @Query("MATCH (organization:Organization)-[:HAS_GROUP*1..3]->(group:Group) WHERE id(group)={0} MATCH (organization)-[:CONTACT_ADDRESS]->(contactAddress:ContactAddress) MATCH (contactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) MATCH (contactAddress)-[:MUNICIPALITY]->(municipality:Municipality) RETURN organization,contactAddress,zipCode,municipality")
    OrganizationContactAddress getOrganizationByGroupId(long groupId);

    @Query("MATCH (organization:Organization)-[:" + HAS_GROUP + "]->(group:Group)-[:" + HAS_TEAM + "]->(team:Team) WHERE id(team)={0} RETURN organization")
    Organization getOrganizationByTeamId(long groupId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} WITH organization " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,contactAddress\n" +
            "RETURN municipality as municipality,contactAddress as contactAddress,zipCode as zipCode")
    OrganizationContactAddress getContactAddressOfOrg(long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} WITH organization " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,contactAddress\n" +
            "RETURN {houseNumber:contactAddress.houseNumber,municipalityName: municipality.name, id:id(contactAddress),floorNumber:contactAddress.floorNumber,city:contactAddress.city,zipCodeId:id(zipCode),regionName:contactAddress.regionName,province:contactAddress.province,municipalityName:contactAddress.municipalityName,isAddressProtected:contactAddress.isAddressProtected,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street:contactAddress.street,municipalityId:id(municipality)} as contactAddress")
    Map<String, Object> getContactAddressOfParentOrganization(Long unitId);

    @Query("MATCH (organization:Organization) WHERE id(organization) IN {0}  " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH contactAddress ,organization\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress,organization \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,contactAddress,organization \n" +
            "RETURN id(organization) as organizationId,id(contactAddress) as id,contactAddress.houseNumber as houseNumber,contactAddress.floorNumber as floorNumber," +
            "contactAddress.city as city,id(zipCode) as zipCodeId,contactAddress.regionName as regionName,contactAddress.province as province," +
            " contactAddress.isAddressProtected as isAddressProtected,contactAddress.longitude as longitude," +
            "contactAddress.latitude as latitude,contactAddress.street as street,id(municipality) as municipalityId,municipality.name as municipalityName")
    List<Map<String, Object>> getContactAddressOfParentOrganization(List<Long> unitId);

    @Query("MATCH (unit:Organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(unit)={0} \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{deleted:false}]->(skill:Skill) WITH distinct skill,unit\n" +
            "MATCH (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WITH skill,r,unit\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH unit,skillCategory,skill,r\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=unit.showCountryTags WITH DISTINCT  r,skill,skillCategory,unit,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + ORGANIZATION_HAS_TAG + "]-(unit) WITH  r,skill,unit,skillCategory,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "OPTIONAL MATCH (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(staff) IN {1}\n" +
            "WITH {staff:case when staffSkillRel is null then [] else COLLECT(id(staff)) end} as staff,skillCategory,skill,r,otags,ctags\n" +
            "RETURN {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:COLLECT({id:id(skill),name:case when r is null or r.customName is null then skill.name else r.customName end,description:skill.description,isSelected:case when r is null then false else true end, customName:case when r is null or r.customName is null then skill.name else r.customName end, isEdited:true,staff:staff.staff,tags:ctags+otags})} as data")
    List<Map<String, Object>> getAssignedSkillsOfStaffByOrganization(long unitId, List<Long> staffId);

    @Query("MATCH (organization:Organization) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "]->(skill:Skill) WITH skill,organization\n" +
            "create unique (organization)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{1},lastModificationDate:{2},isEnabled:true,customName:skill.name}]->(skill)")
    void assignDefaultSkillsToOrg(long orgId, long creationDate, long lastModificationDate);

    @Query("MATCH (n:Organization) WHERE id(n)={0}\n" +
            "MATCH (n)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH subType,n\n" +
            "MATCH (subType)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(organizationService:OrganizationService) WITH organizationService,n\n" +
            "create unique (n)-[:" + PROVIDE_SERVICE + "{isEnabled:true,creationDate:{1},lastModificationDate:{2}}]->(organizationService) ")
    void assignDefaultServicesToOrg(long orgId, long creationDate, long lastModificationDate);

    @Query("MATCH (unit:Organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WHERE id(unit)={0} WITH skill,unit,r\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH  unit,skill,skillCategory,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=unit.showCountryTags WITH  unit,skill,skillCategory,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(unit) WITH skill,skillCategory,r,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "RETURN {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,skills:COLLECT(distinct {id:id(skill),name:skill.name,visitourId:skill.visitourId,description:skill.description,customName:r.customName,isEdited:true, tags:ctags+otags})} as data")
    List<Map<String, Object>> getSkillsOfOrganization(long unitId);

    Organization findByKmdExternalId(String kmdExternalId);

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_SUB_ORGANIZATION + "*]->(unit:Organization) WITH org+[unit] as coll\n" +
            "unwind coll as units WITH distinct units\n" +
            "RETURN units")
    List<Organization> getUnitsWithBasicInfo(long organizationId);

    @Query("MATCH (org:Organization)-[r:" + HAS_SUB_ORGANIZATION + "]->(unit:Organization) WHERE id(org)={0} AND id(unit)={1} RETURN count(r)")
    Integer checkParentChildRelation(Long organizationId, Long unitId);

    @Query("MATCH (n:Organization) WHERE id(n)={0} WITH n \n" +
            "MATCH (n)<-[:HAS_SUB_ORGANIZATION*]-(org:Organization{isParentOrganization:true})  WHERE org.isKairosHub =false \n" +

            "MATCH (org)-[:" + HAS_POSITION_CODE + "]->(p:PositionCode {deleted:false}) RETURN p")
    List<PositionCode> getPositionCodesOfParentOrganization(Long organizationId);


    @Query("MATCH (o:Organization {isEnable:true} )-[:" + HAS_POSITION_CODE + "]->(p:PositionCode {deleted:false}) WHERE id(o)={0} RETURN p")
    List<PositionCode> getPositionCodes(Long organizationId);

    @Query(" MATCH (organization:Organization) WHERE id(organization)={0} WITH organization\n" +
            "MATCH (organization)-[r:PROVIDE_SERVICE]->(os) WHERE os.imported=true WITH distinct os,r\n" +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:ORGANIZATION_SUB_SERVICE]->(os)  WITH r,os,organizationService\n" +
            "OPTIONAL MATCH (organizationService)<-[:" + LINK_WITH_EXTERNAL_SERVICE + "]-(ms:OrganizationService)  WITH r,ms,os,organizationService \n" +
            "OPTIONAL MATCH (os)<-[:" + LINK_WITH_EXTERNAL_SERVICE + "]-(mss:OrganizationService)  WITH r,mss,ms,os,organizationService \n" +
            "WITH {children: case when os is NULL then [] else COLLECT({id:id(os),name:os.name,description:os.description," +
            "isEnabled:r.isEnabled,created:r.creationDate,referenceId:id(mss)}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description,referenceId:id(ms)} as selectedServices RETURN {selectedServices:COLLECT(selectedServices)} as data")
    List<Map<String, Object>> getImportedServicesForUnit(long organizationId);

    @Query("MATCH (org:OrganizationType{isEnable:true}) WHERE id(org) in {0} \n" +
            "RETURN count(org) as count ")
    Long findAllOrgCountMatchedByIds(List<Long> Ids);


    @Query("MATCH (o:Organization{deleted:false,boardingCompleted:true}) RETURN id(o)")
    List<Long> findAllOrganizationIds();


    @Query("MATCH (country:Country)<-[:" + COUNTRY + "]-(o:Organization) WHERE id(o)={0}  RETURN id(country) ")
    Long getCountryId(Long organizationId);


    @Query("MATCH (organization:Organization) - [:" + BELONGS_TO + "] -> (country:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(organization)={0} AND et.deleted={1}\n" +
            "RETURN id(et) as id, et.name as name, et.description as description, \n" +
            "et.allowedForContactPerson as allowedForContactPerson, et.allowedForShiftPlan as allowedForShiftPlan, et.allowedForFlexPool as allowedForFlexPool,et.paymentFrequency as paymentFrequency, " +
            "CASE when et.employmentCategories IS NULL THEN [] ELSE et.employmentCategories END as employmentCategories ORDER BY et.name ASC")
    List<Map<String, Object>> getEmploymentTypeByOrganization(Long organizationId, Boolean isDeleted);


    @Query("MATCH (n:Organization) - [r:BELONGS_TO] -> (c:Country)-[r1:HAS_EMPLOYMENT_TYPE]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2}\n" +
            "RETURN et ORDER BY et.name ASC")
//    id(et) as id, et.name as name, et.description as description
    EmploymentType getEmploymentTypeByOrganizationAndEmploymentId(Long organizationId, Long employmentId, Boolean isDeleted);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} WITH organizationService\n" +
            "MATCH (org:Organization) WHERE id(org)={1} WITH org, organizationService\n" +
            "CREATE UNIQUE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(organizationService) SET r.customName=organizationService.name RETURN true")
    Boolean addCustomNameOfServiceForOrganization(Long subServiceId, Long organizationId);

    //    @Query("MATCH (o:Organization)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os:OrganizationService) WHERE id(os)={0} AND id(o) ={1} SET r.customName={2} RETURN os")
    @Query("MATCH (org:Organization),(os:OrganizationService) WHERE  id(org)={1} AND id(os)={0} WITH org,os\n" +
            "MERGE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os) \n" +
            "ON CREATE SET r.customName={2}\n" +
            "ON MATCH SET r.customName={2}\n" +
            " RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfServiceForOrganization(Long serviceId, Long organizationId, String customName);

    @Query("MATCH (o:Organization)-[r:" + PROVIDE_SERVICE + "{isEnabled:true}]->(os:OrganizationService) WHERE id(os)={0} AND id(o) ={1} SET r.customName={2}\n" +
            "RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfSubServiceForOrganization(Long serviceId, Long organizationId, String customName);


    @Query("MATCH (organization:Organization) - [r:BELONGS_TO] -> (country:Country)\n" +
            "WHERE id(organization)={0}\n" +
            "RETURN country")
    Country getCountry(Long organizationId);

    @Query("MATCH (organization:Organization)  WHERE id(organization)={0} \n" +
            " MATCH(organization)<-[:HAS_SUB_ORGANIZATION*]-(parentOrganization:Organization{isKairosHub:false}) \n" +
            " MATCH(parentOrganization)-[r:BELONGS_TO] -> (country:Country)\n" +
            " RETURN country limit 1")
    Country getCountryByParentOrganization(Long organizationId);

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:true})-[:" + BELONGS_TO + "]->(c:Country)  WHERE id(c)={0} WITH org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]->(companyCategory:CompanyCategory) WITH companyCategory, org\n" +
            "MATCH (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) WITH id(ot) as organizationTypeIds,org,companyCategory\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH  COLLECT(id(subType)) as organizationSubTypeIds,organizationTypeIds,org,companyCategory\n" +
            "MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:ZIP_CODE]->(zipCode:ZipCode) WITH organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,companyCategory\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,companyCategory\n" +
            "MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_LEVEL + "]-(level:Level{isEnabled:true}) WITH level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory  ORDER BY org.name\n" +
            "OPTIONAL MATCH (emp:Employment)-[:" + HAS_UNIT_PERMISSIONS + "]->(unitPermission:UnitPermission)-[:" + APPLICABLE_IN_UNIT + "]->(org)\n" +
            "OPTIONAL MATCH (unitPermission)-[r1:" + HAS_ACCESS_GROUP + "]-(ag:AccessGroup{deleted:false, role:'MANAGEMENT'})\n" +
            "OPTIONAL MATCH (emp)-[:" + BELONGS_TO + "]-(staff:Staff)-[:" + BELONGS_TO + "]-(u:User)\n" +
            "WITH COLLECT(u) as unitManagers, COLLECT(ag) as accessGroups,level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "WITH CASE WHEN size(unitManagers)>0 THEN unitManagers[0] ELSE null END as unitManager,\n" +
            "CASE WHEN size(accessGroups)>0 THEN accessGroups[0] ELSE null END as accessGroup,\n" +
            "level,businessTypeIds,organizationSubTypeIds,organizationTypeIds,org,contactAddress,zipCode,municipality,companyCategory\n" +
            "RETURN COLLECT({unitManager:CASE WHEN unitManager IS NULL THEN null ELSE  {id:id(unitManager), email:unitManager.email, firstName:unitManager.firstName, lastName:unitManager.lastName, cprNumber:unitManager.cprNumber, accessGroupId:id(accessGroup), accessGroupName:accessGroup.name} END, id:id(org),levelId:id(level),companyCategoryId:id(companyCategory),businessTypeIds:businessTypeIds,typeId:organizationTypeIds,subTypeId:organizationSubTypeIds,name:org.name,prekairos:org.isPrekairos,kairosHub:org.isKairosHub,description:org.description,externalId:org.externalId,desiredUrl:org.desiredUrl,shortCompanyName:org.shortCompanyName,kairosCompanyId:org.kairosCompanyId,companyType:org.companyType,vatId:org.vatId,costCenter:org.costCenter,costCenterId:org.costCenterId,companyUnitType:org.companyUnitType,contactAddress:{houseNumber:contactAddress.houseNumber,floorNumber:contactAddress.floorNumber,city:contactAddress.city,zipCode:id(zipCode),regionName:contactAddress.regionName,province:contactAddress.province,municipalityName:contactAddress.municipalityName,isAddressProtected:contactAddress.isAddressProtected,longitude:contactAddress.longitude,latitude:contactAddress.latitude,street:contactAddress.street,municipalityId:id(municipality)}}) as organizations")
    OrganizationQueryResult getAllUnionOfCountry(long countryId);

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:true})-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(subType) IN {0} " +
            "RETURN  id(org) as id,org.name as name")
    List<UnionResponseDTO> getAllUnionsByOrganizationSubType(List<Long> organizationSubTypesId);


    @Query("MATCH (o:Organization)-[rel:" + ORGANIZATION_HAS_UNIONS + "]->(union:Organization) WHERE id(o)={0}  AND  id(union)={1} \n" +
            "SET rel.disabled=true, rel.dateOfSeparation={2} ")
    void removeUnionFromOrganization(long unitId, long unionId, long dateOfSeparation);

    @Query("MATCH (unit:Organization),(union:Organization) WHERE id (unit)={0} AND id(union) = {1} merge (unit)-[r:" + ORGANIZATION_HAS_UNIONS + "{dateOfJoining:{2},disabled:false}]->(union)")
    void addUnionInOrganization(long unitId, Long unionId, long dateOfJoining);


    @Query("MATCH (union:Organization{union:true,isEnable:true}) WHERE id (union)={0}  RETURN union")
    Organization findByIdAndUnionTrueAndIsEnableTrue(Long unionId);


    @Query("MATCH(o:Organization)-[:" + HAS_SUB_ORGANIZATION + "*]->(s:Organization{isEnable:true,isKairosHub:false,union:false}) WHERE id(o)={0} \n" +
            "RETURN s.name as name ,id(s) as id")
    List<OrganizationBasicResponse> getOrganizationHierarchy(Long parentOrganizationId);

    @Query("MATCH(o:Organization)-[:HAS_SUB_ORGANIZATION]-(parentOrganization:Organization{isEnable:true,isKairosHub:false,union:false}) WHERE id(o)={0} \n"
            + "MATCH(parentOrganization)-[:" + HAS_SUB_ORGANIZATION + "]-(units:Organization{isEnable:true,isKairosHub:false,union:false}) " +
            " WITH parentOrganization ,COLLECT (units)  as data " +
            " RETURN parentOrganization as parent,data as childUnits")
    OrganizationHierarchyData getChildHierarchyByChildUnit(Long childUnitId);

    // For Test Cases

    @Query("MATCH (org:Organization{union:false,isKairosHub:false,isEnable:true})-[:" + COUNTRY + "]-(c:Country) WHERE id (c)={0}  RETURN org LIMIT 1")
    Organization getOneParentUnitByCountry(Long countryId);

    //for getting Unions by Ids

    @Query("MATCH (union:Organization{union:true,isEnable:true}) WHERE id (union) IN {0}  RETURN union")
    List<Organization> findUnionsByIdsIn(List<Long> unionIds);

    @Query("MATCH (union:Organization{isEnable:true,union:true})-[:" + BELONGS_TO + "]->(country:Country)  WHERE id(country)={0} RETURN id(union) as id, union.name as name")
    List<UnionQueryResult> findAllUnionsByCountryId(Long countryId);

    // This 8 is hardCoded because we only need to get the last Integer value of the organization's company Id
    // OOD-KAI-01    OOD-KAI-    >>  8
    @Query("OPTIONAL MATCH (org:Organization{isEnable:true}) WHERE org.desiredUrl=~{0} WITH  case when count(org)>0 THEN  true ELSE false END as desiredUrl\n" +
            "OPTIONAL MATCH (org:Organization{isEnable:true}) WHERE org.name =~{1} WITH desiredUrl, case when count(org)>0 THEN  true ELSE false END as name\n" +
            "OPTIONAL MATCH(org:Organization)" +
            "RETURN name,desiredUrl,org.kairosCompanyId as kairosCompanyId  ORDER BY subString(org.kairosCompanyId,8,size(org.kairosCompanyId)) DESC LIMIT 1")
    CompanyValidationQueryResult checkOrgExistWithUrlOrName(String desiredUrl, String name, String first3Char);

    @Query("MATCH (org:Organization{isEnable:true}) WHERE org.desiredUrl={0}\n" +
            "RETURN case when count(org)>0 THEN  true ELSE false END as response")
    Boolean checkOrgExistWithUrl(String desiredUrl);

    @Query("MATCH (org:Organization{isEnable:true}) WHERE org.name={0}\n" +
            "RETURN case when count(org)>0 THEN  true ELSE false END as response")
    Boolean checkOrgExistWithName(String name);

    @Query("MATCH (org:Organization)-[:" + HAS_SETTING + "]-(orgSetting:OrganizationSetting) WHERE id(org)={0} RETURN orgSetting")
    OrganizationSetting getOrganisationSettingByOrgId(Long unitId);

    @Query("MATCH (org:Organization)-[:" + SUB_TYPE_OF + "]->(orgType:OrganizationType) WHERE id(orgType) IN {0} \n" +
            "MATCH(org)-[:" + SUB_TYPE_OF + "]->(organizationType:OrganizationType) \n" +
            "RETURN id(org) as unitId, COLLECT(id(organizationType)) as orgTypeIds")
    List<OrgTypeQueryResult> getOrganizationIdsBySubOrgTypeId(List<Long> organizationSubTypeId);

    @Query("MATCH (child:Organization) \n" +
            "OPTIONAL MATCH (child)<-[:" + HAS_SUB_ORGANIZATION + "]-(parent:Organization) WITH child,parent\n" +
            "MATCH (child)<-[:" + HAS_SUB_ORGANIZATION + "]-(superParent:Organization) WITH child,parent,superParent\n" +
            "MATCH (superParent)-[:" + BELONGS_TO + "]-(country:Country)\n" +
            "RETURN id(child) as unitId, CASE WHEN parent IS NULL THEN id(child) ELSE id(parent) END as parentOrganizationId, id(country) as countryId")
    List<Map<String, Object>> getUnitAndParentOrganizationAndCountryIds();


    @Query("MATCH (org:Organization) WHERE id(org)={0} \n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]->(companyCategory:CompanyCategory) WITH companyCategory, org\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]->(accountType:AccountType) WITH companyCategory,accountType, org\n" +
            "OPTIONAL MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,org,companyCategory,accountType\n" +
            "RETURN id(org) as id,org.kairosId as kairosId,id(companyCategory) as companyCategoryId,businessTypeIds as businessTypeIds,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,org.desiredUrl as desiredUrl," +
            "org.shortCompanyName as shortCompanyName,org.kairosCompanyId as kairosCompanyId,org.companyType as companyType,org.vatId as vatId," +
            "org.companyUnitType as companyUnitType,id(accountType) as accountTypeId")
    OrganizationBasicResponse getOrganizationDetailsById(Long organizationId);

    @Query("MATCH (organization:Organization) WHERE id(organization) IN {0} " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH organization,contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress,organization \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) \n" +
            "RETURN organization as organization, municipality as municipality,contactAddress as contactAddress,zipCode as zipCode")
    List<OrganizationContactAddress> getContactAddressOfOrganizations(List<Long> unitIds);


    @Query("MATCH (parent:Organization) WHERE id(parent)={0} \n" +
            "OPTIONAL MATCH (parent)-[:HAS_SUB_ORGANIZATION]->(child:Organization) WITH child\n" +
            "OPTIONAL MATCH (child)-[rel:HAS_ACCOUNT_TYPE]->(accountType:AccountType) \n" +
            " DETACH DELETE rel \n" +
            "WITH child MATCH(accountType:AccountType) WHERE id(accountType)={1}\n" +
            "MERGE (child)-[:HAS_ACCOUNT_TYPE]->(accountType)")
    void updateAccountTypeOfChildOrganization(Long parentOrganization, Long accountTypeId);

    @Query("MATCH (org:Organization) WHERE id(org) IN {0} DETACH DELETE org")
    void removeOrganizationCompletely(List<Long> organizationIdsToDelete);

    @Query("MATCH(org:Organization{deleted:false}) RETURN id(org) as unitId, org.timeZone as timezone ORDER BY unitId")
    List<UnitTimeZoneQueryResult> findTimezoneforAllorganizations();

    @Query("MATCH(union:Organization{deleted:false,union:true}) WHERE id(union)={0} or union.name={1} WITH union MATCH(union)-[:BELONGS_TO]-(country:Country) WITH union,country OPTIONAL " +
            "MATCH(union)-[:"+HAS_SECTOR+"]-(sector:Sector) WITH union,collect(sector) as sectors,country OPTIONAL MATCH(union)-[:"+CONTACT_ADDRESS+"]-" +
            "(address:ContactAddress) OPTIONAL MATCH(address)-[:"+ZIP_CODE+"]-(zipCode:ZipCode) WITH union,sectors,address,zipCode,country OPTIONAL MATCH(address)-[:"+MUNICIPALITY+"]-" +
            "(municipality:Municipality) RETURN union,country,address,zipCode,sectors,municipality ")
    List<UnionDataQueryResult> getUnionCompleteById(Long unionId, String name);

    @Query("MATCH(union:Organization{deleted:false,union:true}) WHERE id(union)={1} MATCH(union)-[unionSectorRelDel:"+HAS_SECTOR+"]-(sector:Sector) WHERE id(sector) in {0} WITH union, " +
            "unionSectorRelDel DELETE unionSectorRelDel")
    void deleteUnionSectorRelationShip(List<Long> deleteSectorIds,Long unionId);

    @Query("MATCH(union:Organization) WHERE id(union)={1} MATCH(sector:Sector) WHERE id(sector)in {0} create unique (union)-[:"+HAS_SECTOR+"]-(sector)")
    void createUnionSectorRelationShip(List<Long> createSectorIds,Long unionId);

    @Query("MATCH(union:Organization{deleted:false,union:true}) WHERE union.name={0} RETURN count(union)>0")
    boolean existsByName(String name);

    @Query("MATCH(union:Organization{union:true,deleted:false})-[:BELONGS_TO]-(country:Country) WHERE id(country)={0}\n" +
            "WITH union OPTIONAL MATCH(union)-[:HAS_SECTOR]-(sector:Sector) WITH union,collect(sector) as sectors OPTIONAL MATCH(union)-[:CONTACT_ADDRESS]-" +
            "(address:ContactAddress) WITH union,sectors,address OPTIONAL MATCH(address)-[:ZIP_CODE]-(zipCode:ZipCode) WITH union,sectors,address,zipCode\n" +
            "OPTIONAL MATCH(address)-[:MUNICIPALITY]-(municipality:Municipality) WITH union,sectors,address,zipCode,municipality OPTIONAL MATCH(zipCode)-\n" +
            "[:MUNICIPALITY]-(linkedMunicipality:Municipality) WITH union,sectors,address,zipCode,municipality,collect(linkedMunicipality)as municipalities\n" +
            "OPTIONAL MATCH(union)-[:HAS_LOCATION]-(location:Location{deleted:false}) RETURN union,sectors,address,zipCode,municipality,municipalities,collect(location) as locations")
    List<UnionDataQueryResult> getUnionData(Long countryId);

    @Query("MATCH(union:Organization{deleted:false}) WHERE id(union)={0} RETURN union.boardingCompleted")
    boolean isPublishedUnion(Long unionId);

    @Query("MATCH(union:Organization) WHERE id(union)={0} " +
            "MATCH(sector:Sector) WHERE id(sector)={1} " +
            "CREATE UNIQUE (union)-[:HAS_SECTOR]-(sector)")
    void linkUnionSector(Long unionId,Long sectorId);



    @Query("MATCH(parentOrg:Organization{isEnable:true,boardingCompleted: true}) WHERE id(parentOrg)={0}\n" +
            "MATCH (parentOrg)-[:HAS_SUB_ORGANIZATION*]->(subOrg:Organization{isEnable:true,boardingCompleted: true}) \n" +
            "OPTIONAL MATCH(parentOrganizationType:OrganizationType{deleted:false})<-[:TYPE_OF]-(parentOrg)-[:SUB_TYPE_OF]->(parentSubOrganizationType:OrganizationType{deleted:false})\n" +
            "OPTIONAL MATCH(childOrganizationType:OrganizationType{deleted:false})<-[:TYPE_OF]-(subOrg)-[:SUB_TYPE_OF]->(childSubOrganizationType:OrganizationType{deleted:false})\n" +
            "OPTIONAL MATCH(parentOrganizationService:OrganizationService{deleted:false})<-[:HAS_CUSTOM_SERVICE_NAME_FOR]-(parentOrg)-[:PROVIDE_SERVICE]->(parentSubOrganizationService:OrganizationService{deleted:false})\n" +
            "OPTIONAL MATCH(childOrganizationService:OrganizationService{deleted:false})<-[:HAS_CUSTOM_SERVICE_NAME_FOR]-(subOrg)-[:PROVIDE_SERVICE]->(childSubOrganizatioonService:OrganizationService{deleted:false})\n" +
            "OPTIONAL MATCH(parentAccountType:AccountType{deleted:false})<-[:HAS_ACCOUNT_TYPE]-(parentOrg)\n" +
            "OPTIONAL MATCH(childAccountType:AccountType{deleted:false})<-[:HAS_ACCOUNT_TYPE]-(subOrg)\n" +
            "RETURN \n" +
            "{organizationType:CASE WHEN parentOrganizationType IS NULL THEN COLLECT(DISTINCT {id:id(childOrganizationType),name:childOrganizationType.name})  ELSE COLLECT(DISTINCT {id:id(parentOrganizatinType),name:parentOrganizationType.name})  END,\n" +
            "organizationSubType:CASE WHEN parentSubOrganizationType IS NULL THEN COLLECT(DISTINCT {id:id(childSubOrganizationType),name:childSubOrganizationType.name})  ELSE COLLECT(DISTINCT {id:id(parentSubOrganizatinType),name:parentSubOrganizationType.name})  END,\n" +
            "organizationService:CASE WHEN parentOrganizationService IS NULL THEN COLLECT(DISTINCT {id:id(childOrganizationService),name:childOrganizationService.name})  ELSE COLLECT(DISTINCT {id:id(parentOrganizatinType),name:parentOrganizationType.name})  END,\n" +
            "organizationSubService:CASE WHEN parentSubOrganizationService IS NULL THEN COLLECT(DISTINCT {id:id(childSubOrganizationService),name:childSubOrganizationService.name})  ELSE COLLECT(DISTINCT {id:id(parentSubOrganizationService),name:parentSubOrganizationService.name})  END,\n" +
            "accountType:CASE WHEN parentAccountType IS NULL THEN COLLECT(DISTINCT {id:id(childAccountType),name:childAccountType.name})  ELSE COLLECT(DISTINCT {id:id(parentAccountType),name:parentAccountType.name})  END}")
    Map<String,Object> getFiltersByParentOrganizationId(long parentOrganizationId);


    @Query("MATCH (organizations:Organization{deleted:false,isEnable:true}) WHERE id (organizations) IN {0}  RETURN organizations")
    List<Organization> findOrganizationsByIdsIn(List<Long> orgIds);
}

