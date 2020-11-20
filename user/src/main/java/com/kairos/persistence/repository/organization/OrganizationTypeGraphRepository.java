package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.*;
import com.kairos.persistence.model.organization_type.OrgTypeSkillQueryResult;
import com.kairos.persistence.model.organization_type.OrganizationTypeSubTypeAndServicesQueryResult;
import com.kairos.persistence.model.user.open_shift.OrganizationTypeAndSubType;
import com.kairos.persistence.model.user.skill.SkillCategoryQueryResults;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 14/10/16.
 */
@Repository
public interface OrganizationTypeGraphRepository extends Neo4jBaseRepository<OrganizationType, Long> {

    @Override
    List<OrganizationType> findAll();

    @Query("MATCH (organizationType:OrganizationType) where id(organizationType) in {0} return organizationType")
    List<OrganizationType> findByIdIn(List<Long> ids);

    @Query("MATCH (ot:OrganizationType{isEnable:true})-[:" + BELONGS_TO + "]->(c:Country) WHERE id(c)= {0}\n" +
            "Optional Match (ot)-[:" + HAS_LEVEL + "]->(level:Level{deleted:false,isEnabled:true}) return " +
            "ot.translations as translations,  " +
            "ot.name as name,id(ot) as id,collect(level) as levels")
    List<OrgTypeLevelWrapper> getOrganizationTypeByCountryId(Long countryId);

    OrganizationType findByName(OrganizationType.OrganizationTypeEnum name);

    @Query("Match (o:OrganizationType)-[rel:"+ORGANIZATION_TYPE_HAS_SERVICES+"]->(os:OrganizationService) where id(o)={0}  AND  id(os)={1} return SIGN(COUNT(rel))")
    int checkIfServiceExistsWithOrganizationType(long orgTypeId, long serviceId);


    @Query("Match (o:OrganizationType)-[rel:ORGANIZATION_TYPE_HAS_SERVICES]->(os:OrganizationService) where id(o)={0}  AND  id(os)={1} DELETE rel ")
    void deleteService(long orgTypeId, long serviceId);

    @Query(" Match (o:OrganizationType),(os:OrganizationService) where id(o)= {0} AND id(os)={1}  " +
            " CREATE unique (o)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(os) ")
    void selectService(long orgTypeId, long serviceId);

    @Query(" Match (o:OrganizationType),(os:OrganizationService) where id(o) IN {0} AND id(os)={1}  " +
            "CREATE unique (o)-[:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(os) ")
    void linkOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId);

    @Query("Match (o:OrganizationType)-[rel:" + ORGANIZATION_TYPE_HAS_SERVICES + "]->(os:OrganizationService) where id(o) IN {0}  AND  id(os)={1} DELETE rel ")
    void deleteRelOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId);

    @Query("MATCH (pot:OrganizationType {isEnable:true})-[:HAS_SUB_TYPE]-(ot:OrganizationType{isEnable:true}) WHERE id(pot)={0} return ot.translations as translations,\n" +
            "ot.name as name,id(ot) as id,ot.description as description")
    List<OrgTypeLevelWrapper> getOrganizationSubTypeByTypeId(Long organizationTypeId);

    @Query("MATCH (pot:OrganizationType),(ot:OrganizationType) WHERE id(ot)={0} AND id(pot)={1} Create (pot)-[:HAS_SUB_TYPE]->(ot) return ot")
    OrganizationType createSubTypeRelation(Long subTypeId, Long parentTypeId);

    @Query("match(country:Country) where id(country)={0}\n" +
            "match(country)<-[:" + BELONGS_TO + "]-(organizationType:OrganizationType{isEnable:true})\n" +
            "optional match(organizationType)-[:" + HAS_SUB_TYPE + "]->(organizationSubType:OrganizationType{isEnable:true}) " +
            "with DISTINCT organizationType, organizationSubType " +
            "return organizationType.translations as translations,\n" +
            "id(organizationType) as id, organizationType.name as name , CASE WHEN organizationSubType IS NOT NULL THEN collect({" +
            "translations:organizationSubType.translations,\n" +
            "id:id(organizationSubType),name:organizationSubType.name}) ELSE [] END as children \n")
    List<OrganizationTypeAndSubType> getAllOrganizationTypeAndSubType(long countryId);

    @Query("Match (organization:Organization) where id(organization)={0} \n" +
            "OPTIONAL Match (organization)-[:"+TYPE_OF+"]->(organizationType:OrganizationType) with organizationType,organization\n" +
            "OPTIONAL match (subType:OrganizationType)<-[:"+SUB_TYPE_OF+"]-(organization) with subType,organizationType,organization\n" +
            "OPTIONAL Match (organization)-[:" + HAS_LEVEL + "]->(level:Level{deleted:false}) \n" +
            "return id(organizationType) as id,organizationType.name as name,case when subType is null then [] else collect({id:id(subType),name:subType.name}) end as children,id(level) as levelId")
    OrganizationTypeAndSubType getOrganizationTypesForUnit(long organizationId);

    @Query("Match (organizationType:OrganizationType{isEnable:true})-[:BELONGS_TO]->(country:Country) where id(country)={0}\n" +
            "optional Match (organizationType)-[:HAS_SUB_TYPE]->(subType:OrganizationType{isEnable:true}) with {subTypes:case when subType is null then [] else collect({id:id(subType),name:subType.name,isSelected:id(subType) IN {1}}) end} as subTypes,organizationType\n" +
            "return collect({id:id(organizationType),name:organizationType.name,children: subTypes.subTypes}) as organizationTypes")
    OrganizationTypeHierarchyQueryResult getOrganizationTypeHierarchy(long countryId, Set<Long> subTypesId);


    @Query("Match (orgType:OrganizationType),(skill:Skill) where id (orgType)={0} AND id(skill)={1} " +
            "MERGE (orgType)-[r:" + ORG_TYPE_HAS_SKILL + "]->(skill)" +
            "ON CREATE SET r.creationDate = {2},r.deleted=false\n" +
            "ON MATCH SET r.lastModificationDate = {3},r.deleted=false")
    void addSkillInOrgType(long orgTypeId, long skillId, Long creationDate, Long lastModificationDate);

    @Query("MATCH (orgType:OrganizationType),(skill:Skill) where id(orgType)={0} AND id(skill)={1} \n" +
            "MATCH (orgType)-[r:"+ORG_TYPE_HAS_SKILL+"]->(skill) \n" +
            "MATCH(orgType)<-[:"+SUB_TYPE_OF+"]-(organization:OrganizationBaseEntity)-[rel:"+ORGANISATION_HAS_SKILL+"]->(skill)\n" +
            "SET r.deleted=true,r.lastModificationDate={2} , rel.isEnabled=false, rel.lastModificationDate={2} ")
    void deleteSkillFromOrgType(long orgTypeId, long skillId, Long lastModificationDate);

    @Query("Match (orgType:OrganizationType)-[r:ORG_TYPE_HAS_SKILL{deleted:false}]->(skill{isEnabled:true}) where id(orgType)={0} " +
            "MATCH (skillCategory:SkillCategory)<-[:HAS_CATEGORY]-(skill) \n" +
            "return  case when skill is NULL then [] else collect(skill) END as  skillList  ,skillCategory.name as name ,id(skillCategory) as id,skillCategory.description as description," +
            "skillCategory.translations as translations")
    List<SkillCategoryQueryResults> getSkillsOfOrganizationType(long orgTypeId);

    @Query("Match (n:Unit{isEnable:true,union:false,boardingCompleted:true,isKairosHub:false,gdprUnit:false})-[:"+SUB_TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) where id(organizationType)={0} return DISTINCT n")
    List<Unit> getOrganizationsByOrganizationType(long orgTypeId);

    @Query("Match (n:Unit{isEnable:true})-[:"+SUB_TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) where id(organizationType)={0} return DISTINCT n")
    List<Unit> getOrganizationsByOrganizationTypeId(long orgTypeId);

    @Query("Match (organization{isEnable:true}) where id(organization)={0} with organization\n" +
            "Match (organization)-[:"+TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) with organizationType,organization\n" +
            "return id(organizationType)")
    List<Long> getOrganizationTypeIdsByUnitId(long unitId);

    @Query("Match (organization{isEnable:true}) where id(organization)={0} with organization\n" +
            "Match (organization)-[:SUB_TYPE_OF]->(subType:OrganizationType{isEnable:true}) with subType,organization\n" +
            "return id(subType)")
    List<Long> getOrganizationSubTypeIdsByUnitId(long unitId);

    @Query("Match (ot:OrganizationType{isEnable:true})-[:" + HAS_LEVEL + "]->(level:Level{deleted:false}) where id(ot)={0} AND id(level)={1} return level")
    Level getLevel(Long organizationTypeId, Long levelId);

    @Query("match(country:Country) where id(country)={0}\n" +
            "match(country)<-[:" + BELONGS_TO + "]-(orgType:OrganizationType{isEnable:true})\n" +
            "optional match(orgType)-[:" + HAS_SUB_TYPE + "]->(orgSubType:OrganizationType{isEnable:true})\n" +
            "WITH {name: orgType.name,id:id(orgType),children:CASE WHEN orgSubType IS NOT NULL THEN collect({id:id(orgSubType),name:orgSubType.name}) ELSE [] END} as organizationType\n" +
            "RETURN organizationType as result")
    List<Map<String, Object>> getAllOrganizationTypeWithSubTypeByCountryId(Long countryId);


    @Query("match(country:Country) where id(country)={0} \n" +
            "match(country)<-[:" + BELONGS_TO + "]-(orgType:OrganizationType{isEnable:true}) WHERE LOWER(orgType.name)=LOWER({1}) return orgType")
    OrganizationType findByName(Long countryId, String name);

    @Query("Match (ot:OrganizationType{isEnable:true})-[rel:" + HAS_LEVEL + "]->(level:Level{deleted:false}) where id(ot)={0} AND id(level) IN {1} DETACH DELETE rel")
    void removeLevelRelationshipFromOrganizationType(Long organizationTypeId, List<Long> levelIds);


    //bobby
    @Query("match(c:Country) where id(c)={0}  " +
            "match(c)-[:BELONGS_TO]-(or:OrganizationType{isEnable:true}) " +
            "optional match(or)-[:HAS_SUB_TYPE]-(ora:OrganizationType{isEnable:true})" +
            " optional match(ora)-[:ORGANIZATION_TYPE_HAS_SERVICES]-(oras:OrganizationService)" +
            " optional match(oras)-[:ORGANIZATION_SUB_SERVICE]-(sub:OrganizationService) with or,ora,oras,sub, {name: oras.name,id:id(oras), organizationSubServices: CASE WHEN sub IS NOT NULL THEN collect({id:id(sub),name:sub.name}) ELSE [] END} as service_subService with or,ora,{name: ora.name,id:id(ora)," +
            "organizationServices: CASE WHEN service_subService IS NOT NULL THEN collect (service_subService) ELSE [] END} as service_SubService_ORG with or,{name: or.name,id:id(or),organizationSubTypes: CASE WHEN service_SubService_ORG IS NOT NULL THEN collect (service_SubService_ORG) ELSE [] END} as organizationType return organizationType")
    List<Map> getAllOrganizationTypeAndServiceAndSubServices(Long countryId);

    @Query("MATCH (organizationSubType:OrganizationType)<-[:" +HAS_SUB_TYPE + "]-(organizationType:OrganizationType) where id(organizationSubType)={0} return id(organizationType)")
    Long findOrganizationTypeIdBySubTypeId(Long organizationSubTypeId);

    @Query("MATCH (organizationSubType:OrganizationType)<-[:" +HAS_SUB_TYPE + "]-(organizationType:OrganizationType) where id(organizationSubType)={0} return organizationType")
    OrganizationType findOrganizationTypeBySubTypeId(Long organizationSubTypeId);

    @Query("Match (unit:Unit{isEnable:true,boardingCompleted:true,gdprUnit:false}) \n" +
            "-[:" +SUB_TYPE_OF + "]->(organizationType:OrganizationType{isEnable:true}) where id(organizationType) in {0} \n" +
            "MATCH(unit)-[:" +PROVIDE_SERVICE + "]-(orgSubService:OrganizationService) where id(orgSubService) in {1} \n" +
            "RETURN distinct id(unit)")
    List<Long> getOrganizationIdsByOrgSubTypeIdsAndOrgSubServiceIds(List<Long> organizationSubTypeIds, List<Long> organizationSubServicesIds);

    /*
    * created by bobby
    * */
    @Query("Match (organization{isEnable:true}) where id(organization)={0}\n" +
            " Match (organization)-[:"+TYPE_OF+"]->(organizationType:OrganizationType{isEnable:true}) \n" +
            " optional match(organizationType)-[:"+HAS_SUB_TYPE+"]->(organizationSubType:OrganizationType{isEnable:true})\n" +
            " with DISTINCT organizationType, organizationSubType\n" +
            " optional match  (organizationSubType)-[:"+ORGANIZATION_TYPE_HAS_SERVICES+"]->( organizationSubService:OrganizationService{isEnabled:true}) with  DISTINCT organizationType, organizationSubType ,organizationSubService      \n" +
            "optional match (organizationSubService)<-[:"+ORGANIZATION_SUB_SERVICE+"]-(organizationService:OrganizationService {isEnabled:true} )             \n" +
            "return id(organizationType) as id, organizationType.name as name ,\n" +
            "collect(distinct {id:id(organizationSubType),name:organizationSubType.name}) as organizationSubTypes,\n" +
            "collect(distinct {id:id(organizationService),name:organizationService.name})  as organizationServices,\n "+
            "collect(distinct { id:id(organizationSubService),name:organizationSubService.name}) as organizationSubServices")
    OrganizationTypeSubTypeAndServicesQueryResult getOrganizationTypeSubTypesServiceAndSubServices(Long unitId);
}
