package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.client.ContactAddress;
import com.kairos.persistence.model.country.Country;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization.company.CompanyValidationQueryResult;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.union.UnionDataQueryResult;
import com.kairos.persistence.model.organization.union.UnionQueryResult;
import com.kairos.persistence.model.organization.union.UnionResponseDTO;
import com.kairos.persistence.model.query_wrapper.OrganizationCreationData;
import com.kairos.persistence.model.query_wrapper.OrganizationWrapper;
import com.kairos.persistence.model.user.counter.OrgTypeQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Interface for CRUD operation on Organization
 */
@Repository
public interface UnitGraphRepository extends Neo4jBaseRepository<Unit, Long>, CustomUnitGraphRepository {

    Unit findByName(String name);

    Unit findByExternalId(String externalId);

    @Query("MATCH (org:Organization {isEnable:true} ),(unit:Unit) WHERE id(org)={0} AND id(unit)={1} CREATE UNIQUE(org)-[:"+HAS_UNIT+"]->(unit)")
    void createChildOrganization(long parentOrganizationId, long childOrganizationId);

    @Query("MATCH(unit:Unit{deleted:false,union:true}) WHERE  unit.name=~{0} RETURN count(unit)>0")
    boolean existsByName(String name);

    @Query("MATCH (organization:Organization)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) WHERE id(organization)={0} WITH subType,organization\n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{deleted:false}]->(skill:Skill{isEnabled:true}) WITH distinct skill,organization\n" +
            "MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) WITH DISTINCT skill,r, organization\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=true WITH  skill,r,organization,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[ORGANIZATION_HAS_TAG]-(organization) WITH  skill,r,organization,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH\n" +
            " {id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags+otags})} as availableSkills\n" +
            " RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            " UNION\n" +
            " MATCH (unit:Unit)-[:"+SUB_TYPE_OF+"]->(subType:OrganizationType) WHERE id(unit)={1} WITH subType,unit\n" +
            " MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL+"{deleted:false}]->(skill:Skill{isEnabled:true}) WITH distinct skill,unit\n" +
            " MATCH (unit)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill) WITH skill,unit,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=unit.showCountryTags WITH  skill,unit,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[ORGANIZATION_HAS_TAG]-(unit) WITH  skill,r,unit,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            " MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:COLLECT({id:id(skill),name:skill.name,visitourId:r.visitourId, customName:r.customName, description:skill.description,isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            " RETURN {selectedSkills:COLLECT(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfChildOrganizationWithActualName(long organizationId, long unitId);

    @Query("MATCH (organization:Unit) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WITH DISTINCT skill,organization\n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH distinct skill,skillCategory,organization\n" +
            "OPTIONAL MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) WITH\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:r.customName,description:skill.description,visitourId:r.visitourId,isEdited:true})} as availableSkills\n" +
            "RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(organization)={0} WITH subType,organization\n" +
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
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=true WITH  skill,skillCategory,organization,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) \n" +
            "WITH {id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId,isEdited:true, tags:ctags})} as availableSkills\n" +
            "RETURN {availableSkills:COLLECT(availableSkills)} as data\n" +
            "UNION\n" +
            "MATCH (organization:Unit)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(organization)={0} \n" +
            "MATCH (organization)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) \n" +
            "MATCH (skill{isEnabled:true})-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) \n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=organization.showCountryTags WITH  skill,organization,skillCategory,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag{deleted:false})<-[" + ORGANIZATION_HAS_TAG + "]-(organization) WITH  skill,r,organization,skillCategory,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags WITH\n" +
            "{id:id(skillCategory),name:skillCategory.name,children:COLLECT({id:id(skill),name:skill.name,description:skill.description,visitourId:r.visitourId, customName:r.customName, isEdited:true, tags:ctags+otags})} as selectedSkills\n" +
            "RETURN {selectedSkills:COLLECT(selectedSkills)} as data")
    List<Map<String, Object>> getSkillsOfParentOrganizationWithActualName(long unitId);

    @Query("MATCH (o:Unit)-[:CONTACT_ADDRESS]->(ca:ContactAddress) WHERE id(o)={0} RETURN ca")
    ContactAddress getOrganizationAddressDetails(Long organizationId);

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

    @Query("MATCH (user:User)<-[:" + IS_A + "]-(c:Client)-[:GET_SERVICE_FROM]->(o:Unit)  WHERE id(o)= {0} WITH c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  WITH ca,c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(contactDetail:ContactDetail) WITH contactDetail, ca, c,user\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(civilianStatus:CitizenStatus) WITH civilianStatus, contactDetail, ca, c,user\n" +
            "OPTIONAL MATCH (c)-[:HAS_LOCAL_AREA_TAG]->(lat:LocalAreaTag) WITH lat,  civilianStatus, contactDetail, ca, c,user\n" +
            "RETURN {name:user.firstName+\" \" +user.lastName,id:id(c) , gender:user.gender, cprNumber:user.cprNumber , healthStatus:c.healthStatus,citizenDead:c.citizenDead, phoneNumber:contactDetail.mobilePhone, clientStatus:id(civilianStatus), " +
            "address:ca.houseNumber+\" \" +ca.street1, lat:ca.latitude, lng:ca.longitude, profilePic: {1} + c.profilePic, age:user.age, " +
            "localAreaTag:CASE WHEN lat IS NOT NULL THEN {id:id(lat), name:lat.name} ELSE NULL END}  as Client  ORDER BY c.firstName")
    List<Map<String, Object>> getClientsOfOrganization(Long organizationId, String imageUrl);

    @Query("MATCH (c:Client{citizenDead:false})-[r:GET_SERVICE_FROM]->(o:Unit) WHERE id(o)= {0}  WITH c,r\n" +
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


    @Query("MATCH (org)-[r:"+PROVIDE_SERVICE+"]->(os:OrganizationService) WHERE id(org)={0} AND id(os)={1} SET r.isEnabled=false")
    void removeServiceFromOrganization(long unitId, long serviceId);

    @Query("MATCH (org)-[r:"+PROVIDE_SERVICE+"]->(os:OrganizationService) WHERE id(org)={0} AND id(os)={1} SET r.customName=os.name, r.isEnabled=true")
    void updateServiceFromOrganization(long unitId, long serviceId);

    @Query("MATCH (org)-[r:"+PROVIDE_SERVICE+"]->(os:OrganizationService) WHERE id(org)={0} AND id(os)={1} RETURN count(r) as countOfRel")
    int isServiceAlreadyExist(long unitId, long serviceId);

    @Query("MATCH (o:Unit)-[:" + HAS_SUB_ORGANIZATION + "*..4]-(co:Unit) WHERE id(o)={0}  RETURN " +
            "COLLECT ({name:co.name,id:id(co),level:co.organizationLevel}) as organizationList")
    List<Map<String, Object>> getOrganizationChildList(Long id);


    @Query("MATCH (o:Unit)-[:"+HAS_TEAMS+"]-(t:Team) WHERE id(o)={0} RETURN { id:id(t) , name:t.name} as result")
    List<Map<String, Object>> getUnitTeams(Long unitId);

    @Query("MATCH (o:Unit {isEnable:true})-[:HAS_SETTING]-(os:OrganizationSetting) WHERE id(o)={0} WITH os as setting MATCH (setting)-[:OPENING_HOUR]-(oh:OpeningHours) RETURN oh order by oh.index")
    List<OpeningHours> getOpeningHours(Long organizationId);


    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:false})-[:" + BELONGS_TO + "]->(c:Country)  WHERE id(c)={0} \n" +
            "OPTIONAL MATCH (org)-[:" + TYPE_OF + "]->(ot:OrganizationType) WITH org,ot\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WITH COLLECT(id(subType)) as organizationSubTypeIds,org,ot\n" +
            "OPTIONAL MATCH (org)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH organizationSubTypeIds,org,ot,zipCode\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]-(accountType:AccountType)\n" +
            "RETURN id(org) as id,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,id(ot) as typeId,organizationSubTypeIds as subTypeId," +
            "id(accountType) as accountTypeId ,id(zipCode) as zipCodeId ORDER BY org.name")
    List<OrganizationBasicResponse> getAllParentOrganizationOfCountry(Long countryId);

    @Query("MATCH (organization:Organization)-[:" + HAS_UNIT + "]->(org:Unit{deleted:false}) WHERE id(organization)={0}  \n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]-(companyCategory:CompanyCategory) WITH companyCategory, org\n" +
            "OPTIONAL Match (org)-[:" + HAS_LEVEL + "]->(level:Level{deleted:false}) WITH companyCategory, org, level \n"+
            "OPTIONAL MATCH (org)-[:" + HAS_UNIT_TYPE + "]-(unitType:UnitType) WITH companyCategory, org,unitType, level\n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]-(accountType:AccountType) WITH companyCategory,accountType, org,unitType, level\n" +
            "OPTIONAL MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,org,companyCategory,accountType,unitType, level\n" +
            "OPTIONAL MATCH (org)-[:" + TYPE_OF + "]-(ot:OrganizationType) WITH id(ot) as typeId,businessTypeIds,org,companyCategory,accountType,unitType, level\n" +
            "OPTIONAL MATCH (org)-[:" + SUB_TYPE_OF + "]-(subType:OrganizationType) WITH  COLLECT(id(subType)) as subTypeIds,typeId,businessTypeIds,org,companyCategory,accountType,unitType, level\n" +
            "RETURN id(unitType) as unitTypeId,subTypeIds as subTypeId ,typeId as typeId ,id(org) as id,org.kairosId as kairosId,id(companyCategory) as companyCategoryId, id(level) as levelId, businessTypeIds as businessTypeIds,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,org.desiredUrl as desiredUrl," +
            "id(accountType) as accountTypeId,org.shortCompanyName as shortCompanyName,org.kairosCompanyId as kairosCompanyId,org.companyType as companyType,org.vatId as vatId, org.workcentre as workcentre," +
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

    @Query("MATCH (org:Organization) WHERE id(org)={0} WITH org " +
            "MATCH path=(org)-[:"+HAS_UNIT+"]->(child:Unit{isEnable:true,boardingCompleted:true}) WITH NODES(path) AS np WITH REDUCE(s=[], i IN RANGE(0, LENGTH(np)-2, 1) | s + {p:np[i], c:np[i+1]}) AS cpairs UNWIND cpairs AS pairs WITH DISTINCT pairs AS ps RETURN {parent:{name:ps.p.name,id:id(ps.p)},child:{name:ps.c.name,id:id(ps.c)}} as data")
    List<Map<String, Object>> getSubOrgHierarchy(long organizationId);

    @Query("MATCH (c:Client{healthStatus:'ALIVE'})-[r:GET_SERVICE_FROM]-(o:Unit) WHERE id(o)= {0}  WITH c,r\n" +
            "OPTIONAL MATCH (c)-[:CIVILIAN_STATUS]->(cs:CitizenStatus)  WITH cs,c,r\n" +
            "OPTIONAL MATCH (c)-[:HAS_HOME_ADDRESS]->(ca:ContactAddress)  WITH ca,c,r,cs\n" +
            "OPTIONAL MATCH (ca)-[:ZIP_CODE]->(zipCode:ZipCode) WITH ca,c,r,zipCode,cs \n" +
            "OPTIONAL MATCH (c)-[:HAS_CONTACT_DETAIL]->(cd:ContactDetail)  WITH cd,ca,c,r,zipCode,cs\n" +
            "RETURN {name:c.firstName+\" \"+c.lastName,id:id(c),age:c.age,joiningDate:r.joinDate,gender:c.gender,emailId:cd.privateEmail,  citizenDead:c.citizenDead ,  civilianStatus:cs.name,contactNo:cd.privatePhone,emergencyNo:cd.emergencyPhone,city:zipCode.name,address:ca.street1+\", \"+ca.houseNumber,zipcode:zipCode.zipCode } as Client ORDER BY c.firstName")
    List<Map<String, Object>> getAllClientsOfOrganization(long organizationId);


    @Query("MATCH (cityLevel:Organization),(regionLevel:Organization{organizationLevel:'REGION'}) WHERE id(cityLevel)={0} " +
            "CREATE UNIQUE (regionLevel)-[r:" + HAS_SUB_ORGANIZATION + "]->(cityLevel) RETURN count(r)")
    int linkWithRegionLevelOrganization(long organizationId);

    @Query("MATCH (o:Unit {isEnable:true})-[:HAS_PUBLIC_PHONE_NUMBER]-> (p:PublicPhoneNumber) WHERE p.phoneNumber={0} RETURN o")
    Unit findOrganizationByPublicPhoneNumber(String phoneNumber);


    @Query("MATCH (parentO:Unit)-[rel]-(childO:Unit) WHERE id(parentO)={0} AND id(childO)={1} DELETE rel")
    void deleteChildRelationOrganizationById(long parentOrganizationId, long childOrganizationId);

    @Query("MATCH (childO:Unit) WHERE id(childO)={0} DELETE childO")
    void deleteOrganizationById(long childOrganizationId);

    @Query("MATCH (child:Unit) WHERE id(child)={0} " +
            "MATCH (child)<-[:HAS_SUB_ORGANIZATION]-(n:Organization{organizationLevel:'CITY'}) RETURN n limit 1")
    Unit getParentOrganizationOfCityLevel(long unitId);


    @Query("MATCH (o:Unit)-[rel:" + ORGANISATION_HAS_SKILL + "]->(s:Skill) WHERE id(o)={0}  AND  id(s)={1} \n" +
            "OPTIONAL MATCH (o)-[:" + HAS_SUB_ORGANIZATION + "]->(sub:Unit)-[subRel:" + ORGANISATION_HAS_SKILL + "]->(s) SET rel.isEnabled=false, " +
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

    @Query("MATCH (n) WHERE id(n)={0}\n" +
            "OPTIONAL MATCH (n)-[:" + TYPE_OF + "]->(orgType:OrganizationType) WITH id(orgType) as orgId,n\n" +
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


    @Query("MATCH (organization) WHERE id(organization)={0} " +
            "WITH organization MATCH (organization)-[:"+SUB_TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) " +
            "WITH organizationType,organization MATCH (organizationType)-[:"+ORGANIZATION_TYPE_HAS_SERVICES+"]-(os:OrganizationService{isEnabled:true}) " +
            "WITH os,organization " +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT(distinct {id:id(os),name:os.name,description:os.description}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description} as availableServices RETURN {availableServices:COLLECT(availableServices)} as data\n" +
            "UNION\n" +
            "MATCH (organization:Organization)-[:"+HAS_UNIT+"]-(unit:Unit) WHERE id(organization)={0} " +
            "WITH unit " +
            "MATCH (unit)-[:"+SUB_TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) " +
            "WITH organizationType,unit \n" +
            "MATCH (organizationType)-[r:"+ORGANIZATION_TYPE_HAS_SERVICES+"]-(os:OrganizationService{isEnabled:true}) " +
            "WITH DISTINCT os, r, unit " +
            "MATCH (organizationService:OrganizationService{isEnabled:true})-[:"+ORGANIZATION_SUB_SERVICE+"]->(os) " +
            "WITH os, r, unit, organizationService\n" +
            "OPTIONAL MATCH (unit)-[orgServiceCustomNameRelation:"+HAS_CUSTOM_SERVICE_NAME_FOR+"]-(organizationService:OrganizationService) \n" +
            "WITH {children: case when os is NULL then [] else COLLECT({id:id(os),name:os.name,\n" +
            "customName:CASE WHEN r.customName IS null THEN os.name ELSE r.customName END,description:os.description,isEnabled:r.isEnabled,created:r.creationDate}) END,id:id(organizationService),name:organizationService.name,description:organizationService.description,\n" +
            "customName:CASE WHEN orgServiceCustomNameRelation IS null THEN organizationService.name ELSE orgServiceCustomNameRelation.customName END} as selectedServices RETURN {selectedServices:COLLECT(selectedServices)} as data")
    List<Map<String, Object>> getServicesForParent(long organizationId);

    @Query("MATCH (unit:Unit),(skill:Skill) WHERE id (unit)={0} AND id(skill) IN {1} create (unit)-[r:" + ORGANISATION_HAS_SKILL + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:skill.name}]->(skill)")
    void addSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("MATCH (unit:Unit),(skill:Skill) WHERE id (unit)={0} AND id(skill) IN {1} MATCH (unit)-[r:" + ORGANISATION_HAS_SKILL + "]->(skill) set r.creationDate={2},r.lastModificationDate={3},r.isEnabled=true")
    void updateSkillInOrganization(long unitId, List<Long> skillId, long creationDate, long lastModificationDate);

    @Query("MATCH (org),(organizationService:OrganizationService) WHERE id(org)={0} AND id(organizationService) IN {1} create unique (org)-[r:" + PROVIDE_SERVICE + "{creationDate:{2},lastModificationDate:{3},isEnabled:true, customName:organizationService.name}]->(organizationService)")
    void addOrganizationServiceInUnit(long unitId, List<Long> organizationServiceId, long creationDate, long lastModificationDate);

    @Query("MATCH (o:Unit)-[r:" + ORGANISATION_HAS_SKILL + "]->(os:Skill) WHERE id(o)={0} AND id(os)={1} RETURN count(r) as countOfRel")
    int isSkillAlreadyExist(long unitId, long serviceId);

    @Query("MATCH (organization:Unit) WHERE id(organization)={0} WITH organization MATCH (organization)-[:" + HAS_BILLING_ADDRESS + "]->(billingAddress:ContactAddress) WITH billingAddress \n" +
            "OPTIONAL MATCH (billingAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,billingAddress\n" +
            "OPTIONAL MATCH (billingAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,billingAddress\n" +
            "OPTIONAL MATCH (billingAddress)-[:" + PAYMENT_TYPE + "]->(paymentType:PaymentType) WITH paymentType,zipCode,billingAddress,municipality \n" +
            "OPTIONAL MATCH (billingAddress)-[:" + CURRENCY + "]->(currency:Currency) WITH currency,paymentType,zipCode,billingAddress,municipality\n" +
            "RETURN {id:id(billingAddress),houseNumber:billingAddress.houseNumber,floorNumber:billingAddress.floorNumber,street1:billingAddress.street1,zipCodeId:id(zipCode),city:billingAddress.city,municipalityId:id(municipality),regionName:billingAddress.regionName,province:billingAddress.province,country:billingAddress.country,latitude:billingAddress.latitude,longitude:billingAddress.longitude,paymentTypeId:id(paymentType),currencyId:id(currency),streetUrl:billingAddress.streetUrl,billingPerson:billingAddress.contactPersonForBillingAddress} as data")
    Map<String, Object> getBillingAddress(long unitId);


    @Query("MATCH (unit:Unit) WHERE id(unit)={0} " +
            "MATCH (unit)-[:"+CONTACT_ADDRESS+"]->(contactAddress:ContactAddress) " +
            "MATCH (contactAddress)-[:"+ZIP_CODE+"]->(zipCode:ZipCode) " +
            "MATCH (contactAddress)-[:"+MUNICIPALITY+"]->(municipality:Municipality) " +
            "RETURN unit,contactAddress,zipCode,municipality")
    OrganizationContactAddress getOrganizationByOrganizationId(long organizationId);

    @Query("MATCH (organization:Unit)-[:" + HAS_TEAMS + "]->(team:Team) WHERE id(team)={0} RETURN organization")
    Unit getOrganizationByTeamId(long groupId);

    @Query("MATCH (organization) WHERE id(organization)={0} WITH organization " +
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

    @Query("MATCH (organization) WHERE id(organization) IN {0}  " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH contactAddress ,organization\n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress,organization \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) WITH municipality,zipCode,contactAddress,organization \n" +
            "RETURN id(organization) as organizationId,id(contactAddress) as id,contactAddress.houseNumber as houseNumber,contactAddress.floorNumber as floorNumber," +
            "contactAddress.city as city,id(zipCode) as zipCodeId,contactAddress.regionName as regionName,contactAddress.province as province," +
            " contactAddress.isAddressProtected as isAddressProtected,contactAddress.longitude as longitude," +
            "contactAddress.latitude as latitude,contactAddress.street as street,id(municipality) as municipalityId,municipality.name as municipalityName")
    List<Map<String, Object>> getContactAddressOfParentOrganization(List<Long> unitId);

    @Query("MATCH (org)-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(org)={0} \n" +
            "MATCH (subType)-[:" + ORG_TYPE_HAS_SKILL + "{deleted:false}]->(skill:Skill) WITH distinct skill,org\n" +
            "MATCH (org)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WITH skill,r,org\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH org,skillCategory,skill,r\n" +
            "OPTIONAL MATCH (skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + COUNTRY_HAS_TAG + "]-(c:Country) WHERE tag.countryTag=org.showCountryTags WITH DISTINCT  r,skill,skillCategory,org,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:" + HAS_TAG + "]-(tag:Tag)<-[:" + ORGANIZATION_HAS_TAG + "]-(org) WITH  r,skill,org,skillCategory,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "OPTIONAL MATCH (staff:Staff)-[staffSkillRel:" + STAFF_HAS_SKILLS + "{isEnabled:true}]->(skill) WHERE id(staff) IN {1}\n" +
            "WITH {staff:case when staffSkillRel is null then [] else COLLECT(id(staff)) end} as staff,skillCategory,skill,r,otags,ctags\n" +
            "RETURN {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,children:COLLECT({id:id(skill),name:case when r is null or r.customName is null then skill.name else r.customName end,description:skill.description,isSelected:case when r is null then false else true end, customName:case when r is null or r.customName is null then skill.name else r.customName end, isEdited:true,staff:staff.staff,tags:ctags+otags})} as data")
    List<Map<String, Object>> getAssignedSkillsOfStaffByOrganization(long unitId, List<Long> staffId);


    @Query("MATCH (org)-[r:" + ORGANISATION_HAS_SKILL + "{isEnabled:true}]->(skill:Skill) WHERE id(org)={0} WITH skill,org,r\n" +
            "MATCH (skill)-[:" + HAS_CATEGORY + "]->(skillCategory:SkillCategory{isEnabled:true}) WITH  org,skill,skillCategory,r\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[COUNTRY_HAS_TAG]-(c:Country) WHERE tag.countryTag=org.showCountryTags WITH  org,skill,skillCategory,r,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as ctags\n" +
            "OPTIONAL MATCH (skill:Skill)-[:HAS_TAG]-(tag:Tag)<-[ORGANIZATION_HAS_TAG]-(org) WITH skill,skillCategory,r,ctags,CASE WHEN tag IS NULL THEN [] ELSE COLLECT({id:id(tag),name:tag.name,countryTag:tag.countryTag}) END as otags\n" +
            "RETURN {id:id(skillCategory),name:skillCategory.name,description:skillCategory.description,skills:COLLECT(distinct {id:id(skill),name:skill.name,visitourId:skill.visitourId,description:skill.description,customName:r.customName,isEdited:true, tags:ctags+otags})} as data")
    List<Map<String, Object>> getSkillsOfOrganization(long unitId);

    @Query("MATCH (org:Unit)-[r:" + HAS_SUB_ORGANIZATION + "]->(unit:Unit) WHERE id(org)={0} AND id(unit)={1} RETURN count(r)")
    Integer checkParentChildRelation(Long organizationId, Long unitId);


    @Query(" MATCH (organization:Unit) WHERE id(organization)={0} WITH organization\n" +
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


    @Query("MATCH (o:Unit{deleted:false,boardingCompleted:true}) RETURN id(o)")
    List<Long> findAllOrganizationIds();


    @Query("MATCH (country:Country)<-[:" + BELONGS_TO + "]-(o:Unit) WHERE id(o)={0}  RETURN id(country) ")
    Long getCountryId(Long organizationId);


    @Query("MATCH (organization:Organization) - [:" + BELONGS_TO + "] -> (country:Country)-[:" + HAS_EMPLOYMENT_TYPE + "]-> (et:EmploymentType)\n" +
            "WHERE id(organization)={0} AND et.deleted={1}\n" +
            "RETURN id(et) as id, et.name as name, et.description as description, \n" +
            "et.allowedForContactPerson as allowedForContactPerson,et.markMainEmployment as markMainEmployment, et.allowedForShiftPlan as allowedForShiftPlan, et.allowedForFlexPool as allowedForFlexPool,et.paymentFrequency as paymentFrequency, " +
            "CASE when et.employmentCategories IS NULL THEN [] ELSE et.employmentCategories END as employmentCategories,et.weeklyMinutes as weeklyMinutes,et.editableAtEmployment as editableAtEmployment,et.mainEmployment as mainEmployment ORDER BY et.name ASC")
    List<Map<String, Object>> getEmploymentTypeByOrganization(Long organizationId, Boolean isDeleted);


    @Query("MATCH (n:Organization) - [r:"+BELONGS_TO+"] -> (c:Country)-[r1:"+HAS_EMPLOYMENT_TYPE+"]-> (et:EmploymentType)\n" +
            "WHERE id(n)={0} AND id(et)={1} AND et.deleted={2}\n" +
            "RETURN et ORDER BY et.name ASC")
    EmploymentType getEmploymentTypeByOrganizationAndEmploymentId(Long organizationId, Long employmentId, Boolean isDeleted);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true})-[:" + ORGANIZATION_SUB_SERVICE + "]->(os:OrganizationService)\n" +
            "WHERE id(os)={0} WITH organizationService\n" +
            "MATCH (org) WHERE id(org)={1} WITH org, organizationService\n" +
            "CREATE UNIQUE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(organizationService) SET r.customName=organizationService.name RETURN true")
    Boolean addCustomNameOfServiceForOrganization(Long subServiceId, Long organizationId);

    //    @Query("MATCH (o:Unit)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os:OrganizationService) WHERE id(os)={0} AND id(o) ={1} SET r.customName={2} RETURN os")
    @Query("MATCH (org:Unit),(os:OrganizationService) WHERE  id(org)={1} AND id(os)={0} WITH org,os\n" +
            "MERGE (org)-[r:" + HAS_CUSTOM_SERVICE_NAME_FOR + "]->(os) \n" +
            "ON CREATE SET r.customName={2}\n" +
            "ON MATCH SET r.customName={2}\n" +
            " RETURN id(os) as id, os.name as name, r.customName as customName, os.description as description")
    OrganizationServiceQueryResult addCustomNameOfServiceForOrganization(Long serviceId, Long organizationId, String customName);

    @Query("MATCH (organization:Unit) - [r:BELONGS_TO] -> (country:Country)\n" +
            "WHERE id(organization)={0}\n" +
            "RETURN country")
    Country getCountry(Long organizationId);

    @Query("MATCH (org:Organization{isEnable:true,isParentOrganization:true,organizationLevel:'CITY',union:true})-[:" + SUB_TYPE_OF + "]->(subType:OrganizationType) WHERE id(subType) IN {0} " +
            "RETURN  id(org) as id,org.name as name")
    List<UnionResponseDTO> getAllUnionsByOrganizationSubType(List<Long> organizationSubTypesId);


    @Query("MATCH(o:Organization)-[:" + HAS_UNIT + "*]->(s:Unit{isEnable:true,isKairosHub:false,union:false,workcentre:true,boardingCompleted:true}) WHERE id(o)={0} \n" +
            "RETURN s.name as name ,id(s) as id")
    List<OrganizationBasicResponse> getOrganizationHierarchy(Long parentOrganizationId);

    @Query("MATCH(o:Unit)<-[:"+HAS_UNIT+"]-(parentOrganization:Organization{isEnable:true,isKairosHub:false,union:false,boardingCompleted:true}) WHERE id(o)={0} \n"
            + "MATCH(parentOrganization)-[:" + HAS_UNIT + "]-(units:Unit{isEnable:true,isKairosHub:false,union:false,workcentre:true,boardingCompleted:true}) " +
            " WITH parentOrganization ,COLLECT (units)  as data " +
            " RETURN parentOrganization as parent,data as childUnits")
    OrganizationHierarchyData getChildHierarchyByChildUnit(Long childUnitId);

    //for getting Unions by Ids

    @Query("MATCH (union:Organization{union:true,isEnable:true}) WHERE id (union) IN {0}  RETURN union")
    List<Unit> findUnionsByIdsIn(List<Long> unionIds);

    @Query("MATCH (union:Organization{isEnable:true,union:true})-[:" + BELONGS_TO + "]->(country:Country)  WHERE id(country)={0} RETURN id(union) as id, union.name as name")
    List<UnionQueryResult> findAllUnionsByCountryId(Long countryId);

    // This 8 is hardCoded because we only need to get the last Integer value of the organization's company Id
    // OOD-KAI-01    OOD-KAI-    >>  8
    @Query("OPTIONAL MATCH (org:Unit{isEnable:true}) WHERE org.desiredUrl=~{0} with org,\n" +
            "CASE WHEN {0} is NOT NULL THEN \n" +
            "    CASE WHEN count(org)>0 THEN  TRUE ELSE FALSE  END\n" +
            "ELSE FALSE END  AS desiredUrl\n" +
            "OPTIONAL MATCH (org:Unit{isEnable:true}) WHERE org.name =~{1} WITH desiredUrl, case when count(org)>0 THEN  true ELSE false END as name\n" +
            "OPTIONAL MATCH(org:Unit)" +
            "RETURN name,desiredUrl,org.kairosCompanyId as kairosCompanyId  ORDER BY subString(org.kairosCompanyId,8,size(org.kairosCompanyId)) DESC LIMIT 1")
    CompanyValidationQueryResult checkOrgExistWithUrlOrName(String desiredUrl, String name, String first3Char);

    @Query("MATCH (org:Unit{isEnable:true}) WHERE org.desiredUrl={0}\n" +
            "RETURN case when count(org)>0 THEN  true ELSE false END as response")
    Boolean checkOrgExistWithUrl(String desiredUrl);

    @Query("MATCH (org:Unit{isEnable:true}) WHERE org.name={0}\n" +
            "RETURN case when count(org)>0 THEN  true ELSE false END as response")
    Boolean checkOrgExistWithName(String name);

    @Query("MATCH (org)-[:" + HAS_SETTING + "]-(orgSetting:OrganizationSetting) WHERE id(org)={0} RETURN orgSetting")
    OrganizationSetting getOrganisationSettingByOrgId(Long unitId);

    @Query("MATCH (org:Unit)-[:" + SUB_TYPE_OF + "]->(orgType:OrganizationType) WHERE id(orgType) IN {0} \n" +
            "MATCH(org)-[:" + SUB_TYPE_OF + "]->(organizationType:OrganizationType) \n" +
            "RETURN id(org) as unitId, COLLECT(id(organizationType)) as orgTypeIds")
    List<OrgTypeQueryResult> getOrganizationIdsBySubOrgTypeId(List<Long> organizationSubTypeId);

    @Query("MATCH (child:Unit) \n" +
            "OPTIONAL MATCH (child)<-[:" + HAS_SUB_ORGANIZATION + "]-(parent:Unit) WITH child,parent\n" +
            "MATCH (child)<-[:" + HAS_SUB_ORGANIZATION + "]-(superParent:Unit) WITH child,parent,superParent\n" +
            "MATCH (superParent)-[:" + BELONGS_TO + "]-(country:Country)\n" +
            "RETURN id(child) as unitId, CASE WHEN parent IS NULL THEN id(child) ELSE id(parent) END as parentOrganizationId, id(country) as countryId")
    List<Map<String, Object>> getUnitAndParentOrganizationAndCountryIds();


    @Query("MATCH (org:Organization)<-[:"+HAS_SUB_ORGANIZATION+"]-(hub:Organization) WHERE id(org)={0} \n" +
            "OPTIONAL MATCH (org)-[:" + HAS_COMPANY_CATEGORY + "]->(companyCategory:CompanyCategory) WITH companyCategory, org,hub \n" +
            "OPTIONAL MATCH (org)-[:" + HAS_ACCOUNT_TYPE + "]->(accountType:AccountType) WITH companyCategory,accountType, org,hub \n" +
            "OPTIONAL MATCH (org)-[:" + BUSINESS_TYPE + "]-(businessType:BusinessType) WITH COLLECT(id(businessType)) as businessTypeIds,org,companyCategory,accountType,hub \n" +
            "RETURN id(org) as id,org.kairosId as kairosId,id(companyCategory) as companyCategoryId,businessTypeIds as businessTypeIds,org.name as name,org.description as description,org.boardingCompleted as boardingCompleted,org.desiredUrl as desiredUrl," +
            "org.shortCompanyName as shortCompanyName,org.kairosCompanyId as kairosCompanyId,org.companyType as companyType,org.vatId as vatId," +
            "org.companyUnitType as companyUnitType,id(accountType) as accountTypeId, id(hub) as hubId ")
    OrganizationBasicResponse getOrganizationDetailsById(Long organizationId);

    @Query("MATCH (organization:Unit) WHERE id(organization) IN {0} " +
            "OPTIONAL MATCH (organization)-[:" + CONTACT_ADDRESS + "]->(contactAddress:ContactAddress) WITH organization,contactAddress \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + ZIP_CODE + "]->(zipCode:ZipCode) WITH zipCode,contactAddress,organization \n" +
            "OPTIONAL MATCH (contactAddress)-[:" + MUNICIPALITY + "]->(municipality:Municipality) \n" +
            "RETURN organization as organization, municipality as municipality,contactAddress as contactAddress,zipCode as zipCode")
    List<OrganizationContactAddress> getContactAddressOfOrganizations(List<Long> unitIds);


    @Query("MATCH (parent:Unit) WHERE id(parent)={0} \n" +
            "OPTIONAL MATCH (parent)-[:HAS_SUB_ORGANIZATION]->(child:Unit) WITH child\n" +
            "OPTIONAL MATCH (child)-[rel:HAS_ACCOUNT_TYPE]->(accountType:AccountType) \n" +
            " DETACH DELETE rel \n" +
            "WITH child MATCH(accountType:AccountType) WHERE id(accountType)={1}\n" +
            "MERGE (child)-[:HAS_ACCOUNT_TYPE]->(accountType)")
    void updateAccountTypeOfChildOrganization(Long parentOrganization, Long accountTypeId);

    @Query("MATCH (org) WHERE id(org) IN {0} DETACH DELETE org")
    void removeOrganizationCompletely(List<Long> organizationIdsToDelete);

    @Query("MATCH(org:Unit{deleted:false}) RETURN id(org) as id, org.timeZone as timezone ORDER BY id")
    List<OrganizationBasicResponse> findTimezoneforAllorganizations();

    @Query("MATCH(org:Unit{deleted:false}) WHERE id(org) IN {0} RETURN id(org) as id, org.timeZone as timezone ORDER BY id")
    List<OrganizationBasicResponse> findTimezoneByUnitIds(Set<Long> unitIds);



    @Query("MATCH(union:Organization{deleted:false,union:true}) WHERE id(union)={1} MATCH(union)-[unionSectorRelDel:"+HAS_SECTOR+"]-(sector:Sector) WHERE id(sector) in {0} WITH union, " +
            "unionSectorRelDel DELETE unionSectorRelDel")
    void deleteUnionSectorRelationShip(List<Long> deleteSectorIds,Long unionId);

    @Query("MATCH(union:Organization) WHERE id(union)={1} MATCH(sector:Sector) WHERE id(sector)in {0} create unique (union)-[:"+HAS_SECTOR+"]-(sector)")
    void createUnionSectorRelationShip(List<Long> createSectorIds,Long unionId);

    @Query("MATCH(union:Organization{union:true,deleted:false})-[:" + BELONGS_TO + "]-(country:Country) WHERE id(country)={0}\n" +
            "WITH union OPTIONAL MATCH(union)-[:" + HAS_SECTOR + "]-(sector:Sector) WITH union,collect(sector) as sectors OPTIONAL MATCH(union)-[:" + CONTACT_ADDRESS + "]-" +
            "(address:ContactAddress) WITH union,sectors,address OPTIONAL MATCH(address)-[:" + ZIP_CODE + "]-(zipCode:ZipCode) WITH union,sectors,address,zipCode\n" +
            "OPTIONAL MATCH(address)-[:" + MUNICIPALITY + "]-(municipality:Municipality) WITH union,sectors,address,zipCode,municipality OPTIONAL MATCH(zipCode)-\n" +
            "[:MUNICIPALITY]-(linkedMunicipality:Municipality) WITH union,sectors,address,zipCode,municipality,collect(linkedMunicipality)as municipalities\n" +
            "OPTIONAL MATCH(union)-[:" + HAS_LOCATION + "]-(location:Location{deleted:false,defaultLocation:false}) RETURN union,sectors,address,zipCode,municipality,municipalities,collect(location) as locations")
    List<UnionDataQueryResult> getUnionData(Long countryId);

    @Query("MATCH(union:Organization{deleted:false}) WHERE id(union)={0} RETURN union.boardingCompleted")
    boolean isPublishedUnion(Long unionId);

    @Query("MATCH(union:Organization) WHERE id(union)={0} " +
            "MATCH(sector:Sector) WHERE id(sector)={1} " +
            "CREATE UNIQUE (union)-[:HAS_SECTOR]-(sector)")
    void linkUnionSector(Long unionId,Long sectorId);



    @Query("MATCH(parentOrg:Organization{isEnable:true,boardingCompleted: true}) WHERE id(parentOrg)={0}\n" +
            "OPTIONAL MATCH (parentOrg)-[:"+HAS_UNIT+"]->(subOrg:Unit{isEnable:true,boardingCompleted: true}) \n" +
            "OPTIONAL MATCH(parentOrganizationType:OrganizationType{deleted:false})<-[:"+TYPE_OF+"]-(parentOrg)-[:SUB_TYPE_OF]->(parentSubOrganizationType:OrganizationType{deleted:false})\n" +
            "OPTIONAL MATCH(childOrganizationType:OrganizationType{deleted:false})<-[:TYPE_OF]-(subOrg)-[:SUB_TYPE_OF]->(childSubOrganizationType:OrganizationType{deleted:false})\n" +
            "OPTIONAL MATCH(parentOrganizationService:OrganizationService{deleted:false})<-[:HAS_CUSTOM_SERVICE_NAME_FOR]-(parentOrg)-[:PROVIDE_SERVICE]->(parentSubOrganizationService:OrganizationService{deleted:false})\n" +
            "OPTIONAL MATCH(childOrganizationService:OrganizationService{deleted:false})<-[:HAS_CUSTOM_SERVICE_NAME_FOR]-(subOrg)-[:PROVIDE_SERVICE]->(childSubOrganizationService:OrganizationService{deleted:false})\n" +
            "OPTIONAL MATCH(parentAccountType:AccountType{deleted:false})<-[:HAS_ACCOUNT_TYPE]-(parentOrg)\n" +
            "OPTIONAL MATCH(childAccountType:AccountType{deleted:false})<-[:HAS_ACCOUNT_TYPE]-(subOrg)\n" +
            "RETURN \n" +
            "{organizationType:CASE WHEN parentOrganizationType IS NULL THEN COLLECT(DISTINCT {id:id(childOrganizationType),name:childOrganizationType.name})  ELSE COLLECT(DISTINCT {id:id(parentOrganizationType),name:parentOrganizationType.name})  END,\n" +
            "organizationSubType:CASE WHEN parentSubOrganizationType IS NULL THEN COLLECT(DISTINCT {id:id(childSubOrganizationType),name:childSubOrganizationType.name})  ELSE COLLECT(DISTINCT {id:id(parentSubOrganizationType),name:parentSubOrganizationType.name})  END,\n" +
            "organizationService:CASE WHEN parentOrganizationService IS NULL THEN COLLECT(DISTINCT {id:id(childOrganizationService),name:childOrganizationService.name})  ELSE COLLECT(DISTINCT {id:id(parentOrganizationType),name:parentOrganizationType.name})  END,\n" +
            "organizationSubService:CASE WHEN parentSubOrganizationService IS NULL THEN COLLECT(DISTINCT {id:id(childSubOrganizationService),name:childSubOrganizationService.name})  ELSE COLLECT(DISTINCT {id:id(parentSubOrganizationService),name:parentSubOrganizationService.name})  END,\n" +
            "accountType:CASE WHEN parentAccountType IS NULL THEN COLLECT(DISTINCT {id:id(childAccountType),name:childAccountType.name})  ELSE COLLECT(DISTINCT {id:id(parentAccountType),name:parentAccountType.name})  END}")
    Map<String,Object> getFiltersByParentOrganizationId(long parentOrganizationId);


    @Query("MATCH (organizations:Unit{deleted:false,isEnable:true}) WHERE id (organizations) IN {0}  RETURN organizations")
    List<Unit> findOrganizationsByIdsIn(List<Long> orgIds);

    @Query("MATCH(organization:Organization),(hub:Organization) WHERE id(organization)={0} AND id(hub)={1} " +
            "CREATE UNIQUE(hub)-[r:"+HAS_SUB_ORGANIZATION+"]->(organization)  ")
    void linkOrganizationToHub(Long organizationId,Long hubId);

    @Query("MATCH(organization:Organization) WHERE organization.isKairosHub=true AND organization.organizationLevel='COUNTRY' RETURN id(organization) as id, organization.name as name, organization.organizationLevel as organizationLevel")
    List<OrganizationWrapper> getAllHubByCountryId(Long countryId);

    @Query("MATCH(organization:Unit)<-[:"+HAS_SUB_ORGANIZATION+"]-(hub:Unit) WHERE id(organization)={0}  RETURN id(hub)")
    Long getHubIdByOrganizationId(Long organizationId);


    @Query("match (staff:Staff)-[:"+BELONGS_TO+"]-(position:Position)-[:"+HAS_UNIT_PERMISSIONS+"]-(up:UnitPermission)-[:"+APPLICABLE_IN_UNIT+"]-(organization:Unit) where id(staff)={0} RETURN id(organization) as id,organization.name as name")
    List<OrganizationWrapper> getAllOrganizaionByStaffid(Long staffId);

}

