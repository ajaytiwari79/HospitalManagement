package com.kairos.persistence.repository.organization;
import com.kairos.persistence.model.organization.OrgTypeExpertiseQueryResult;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.organization.OrganizationTypeHierarchyQueryResult;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_TYPE_HAS_SERVICES;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORG_TYPE_HAS_EXPERTISE;


/**
 * Created by oodles on 14/10/16.
 */
@Repository
public interface OrganizationTypeGraphRepository extends GraphRepository<OrganizationType> {

    @Override
    List<OrganizationType> findAll();

    @Query("MATCH (organizationType:OrganizationType) where id(organizationType) in {0} return organizationType")
    List<OrganizationType> findByIdIn(List<Long> ids);

    @Query("MATCH (ot:OrganizationType{isEnable:true})-[:BELONGS_TO]->(c:Country) WHERE id(c)= {0} return ot")
    List<OrganizationType> getOrganizationTypeByCountryId(Long countryId);

    OrganizationType findByName(OrganizationType.OrganizationTypeEnum name);

    @Query("MATCH (o:OrganizationType) return {id:id(o) , name:o.name} as typeList")
    List<Map<String,Object>> findAllTypes();

    @Query("Match (o:OrganizationType)-[rel:ORGANIZATION_TYPE_HAS_SERVICES]->(os:OrganizationService) where id(o)={0}  AND  id(os)={1} return SIGN(COUNT(rel))")
    int checkIfServiceExistsWithOrganizationType(long orgTypeId, long serviceId);

    @Query("Match (o:OrganizationType)-[rel:ORGANIZATION_TYPE_HAS_SERVICES]->(os:OrganizationService) where id(o)={0}  AND  id(os)={1} DELETE rel ")
    void deleteService(long orgTypeId, long serviceId);

    @Query(" Match (o:OrganizationType),(os:OrganizationService) where id(o)= {0} AND id(os)={1}  " +
            " CREATE unique (o)-[:"+ORGANIZATION_TYPE_HAS_SERVICES+"]->(os) return os")
    void selectService(long orgTypeId, long serviceId);

    @Query(" Match (o:OrganizationType),(os:OrganizationService) where id(o) IN {0} AND id(os)={1}  " +
            "CREATE unique (o)-[:"+ORGANIZATION_TYPE_HAS_SERVICES+"]->(os) return os")
    void linkOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId);

    @Query("Match (o:OrganizationType)-[rel:"+ORGANIZATION_TYPE_HAS_SERVICES+"]->(os:OrganizationService) where id(o) IN {0}  AND  id(os)={1} DELETE rel ")
    void deleteRelOrganizationTypeWithService(Set<Long> orgTypeId, long serviceId);

    @Query("MATCH (pot:OrganizationType {isEnable:true})-[:HAS_SUB_TYPE]-(ot:OrganizationType{isEnable:true}) WHERE id(pot)={0} return {name:ot.name,id:id(ot),description:ot.description } as result")
    List<Map<String,Object>> getOrganizationSubTypeByTypeId(Long organizationTypeId);

    @Query("MATCH (pot:OrganizationType),(ot:OrganizationType) WHERE id(ot)={0} AND id(pot)={1} Create (pot)-[:HAS_SUB_TYPE]->(ot) return ot")
    OrganizationType createSubTypeRelation(Long subTypeId, Long parentTypeId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:TYPE_OF]->(organizationType:OrganizationType) with organizationType,organization\n" +
            "optional match (organizationType)-[:HAS_SUB_TYPE]->(subType:OrganizationType)<-[:SUB_TYPE_OF]-(organization) with subType,organizationType,organization\n" +
            "return {id:id(organizationType),name:organizationType.name,children:case when subType is null then [] else collect({id:id(subType),name:subType.name}) end} as data")
    List<Map<String,Object>> getOrganizationTypesForUnit(long organizationId);

    @Query("Match (organizationType:OrganizationType{isEnable:true})-[:BELONGS_TO]->(country:Country) where id(country)={0}\n" +
            "optional Match (organizationType)-[:HAS_SUB_TYPE]->(subType:OrganizationType{isEnable:true}) with {subTypes:case when subType is null then [] else collect({id:id(subType),name:subType.name,isSelected:id(subType) IN {1}}) end} as subTypes,organizationType\n" +
            "return collect({id:id(organizationType),name:organizationType.name,children: subTypes.subTypes}) as organizationTypes")
    OrganizationTypeHierarchyQueryResult getOrganizationTypeHierarchy(long countryId, Set<Long> subTypesId);

    @Query("Match (orgType:OrganizationType)-[r:"+ORG_TYPE_HAS_EXPERTISE+"]->(expertise:Expertise) where id(orgType)={0} AND id(expertise)={1} return count(r) as countOfRel")
    int orgTypeHasAlreadySkill(long orgTypeId, long expertiseId);

    @Query("Match (orgType:OrganizationType),(expertise:Expertise) where id (orgType)={0} AND id(expertise)={1} create (orgType)-[r:"+ORG_TYPE_HAS_EXPERTISE+"{creationDate:{2},lastModificationDate:{3},isEnabled:true}]->(expertise) return orgType")
    void addExpertiseInOrgType(long orgTypeId, long expertiseId, long creationDate, long lastModificationDate);

    @Query("Match (orgType:OrganizationType),(expertise:Expertise) where id (orgType)={0} AND id(expertise) = {1} Match (orgType)-[r:"+ORG_TYPE_HAS_EXPERTISE+"]->(expertise) set r.lastModificationDate={2},r.isEnabled=true return orgType")
    void updateOrgTypeExpertise(long orgTypeId, long expertiseId, long lastModificationDate);

    @Query("Match (orgType:OrganizationType),(expertise:Expertise) where id(orgType)={0} AND id(expertise)={1} match (orgType)-[r:"+ORG_TYPE_HAS_EXPERTISE+"]->(expertise) set r.isEnabled=false,r.lastModificationDate={2} return r")
    void deleteOrgTypeExpertise(long orgTypeId, long expertiseId, long lastModificationDate);

    @Query("Match (expertise:Expertise{isEnabled:true})-[:BELONGS_TO]->(country:Country) where id(country)={0} with expertise\n" +
            "optional Match (orgType:OrganizationType)-[r:ORG_TYPE_HAS_EXPERTISE]->(expertise) where id(orgType)={1} return collect({id:id(expertise),name:expertise.name,isSelected:case when r.isEnabled then true else false end}) as expertise")
    OrgTypeExpertiseQueryResult getExpertiseOfOrganizationType(long countryId, long orgTypeId);

    @Query("match(o:OrganizationType) where  id(o) in {0} \n" +
            "match(workingTimeAgreement:WorkingTimeAgreement{deleted:false})-[:BELONGS_TO]->(o)\n" +
            "return workingTimeAgreement")
    List<WorkingTimeAgreement> getAllWTAByOrganiationType(List<Long> organizationTypeIds);

    @Query("Match (n:Organization{isEnable:true})-[:SUB_TYPE_OF]->(organizationType:OrganizationType) where id(organizationType)={0} return n")
    List<Organization> getOrganizationsByOrganizationType(long orgTypeId);


    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:TYPE_OF]->(organizationType:OrganizationType) with organizationType,organization\n" +
            "optional match (organizationType)-[:HAS_SUB_TYPE]->(subType:OrganizationType)<-[:SUB_TYPE_OF]-(organization) with subType,organizationType,organization\n" +
            "return id(organizationType)")
    List<Long> getOrganizationTypeIdsByUnitId(long unitId);

    @Query("Match (organization:Organization) where id(organization)={0} with organization\n" +
            "Match (organization)-[:TYPE_OF]->(organizationType:OrganizationType) with organizationType,organization\n" +
            "optional match (organizationType)-[:HAS_SUB_TYPE]->(subType:OrganizationType)<-[:SUB_TYPE_OF]-(organization) with subType,organizationType,organization\n" +
            "return id(subType)")
    List<Long> getOrganizationSubTypeIdsByUnitId(long unitId);
}
