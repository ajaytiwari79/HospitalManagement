package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OrganizationExternalServiceRelationship;
import com.kairos.persistence.model.organization.services.OrganizationService;
import com.kairos.persistence.model.organization.services.OrganizationServiceQueryResult;
import com.kairos.persistence.model.organization.services.OrganizationServicesAndLevelQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 16/9/16.
 */
@Repository
public interface OrganizationServiceRepository extends Neo4jBaseRepository<OrganizationService,Long>{
    List<OrganizationService> findAll();

    @Query("MATCH (c:Country),(n:OrganizationService{isEnabled:true}) where id(c)={0} match (c)-[:HAS_ORGANIZATION_SERVICES]->(n) OPTIONAL MATCH (n)-[:ORGANIZATION_SUB_SERVICE]->(s:OrganizationService{isEnabled:true})" +
            " RETURN {children: case when s is NULL then [] else collect({id:id(s),name:s.name,description:s.description}) END,id:id(n),name:n.name,description:n.description} as result")
    List<Map<String,Object>> getOrganizationServicesByCountryId(long countryId);

    @Query(" MATCH  (o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(o)={0} " +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) " +
            " RETURN {children: case when os  is NULL then [] else collect({id:id(ss),name:ss.name,description:ss.description}) END, id:id(os),name:os.name,description:os.description} as result ")
    List<Map<String,Object>> getOrgServicesByOrgType(long organizationType);

    @Override
    OrganizationService findOne(Long aLong);

    @Query("MATCH (c:Country)-[:"+HAS_ORGANIZATION_SERVICES+"]->(os:OrganizationService{isEnabled:true}) WHERE id(c)={0} AND os.name=~ {1} AND id(os)<>{2} return CASE WHEN COUNT(os)>0 THEN TRUE ELSE FALSE END AS result ")
    boolean checkDuplicateService(long countryId, String name,Long id);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService{isEnabled:true}) WHERE id(os)={0} AND ss.name=~ {1} return ss")
    OrganizationService checkDuplicateSubService(Long id, String name);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService) WHERE id(os)={0} AND ss.name= {1} return ss")
    OrganizationService checkDuplicateSubServiceWithSpecialCharacters(Long id, String name);

    OrganizationService findByKmdExternalId(String kmdExternalId);

    @Query("MATCH (o:Unit)-[r:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) where id(o)={0}  return id(os) as id, r.customName as name, os.description as description")
    List<OrganizationServiceQueryResult> getOrganizationServiceByOrgId(Long organizationId);

    @Query("MATCH (o:Unit)-[r:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) where id(o)={0} AND id(os) IN {1} return id(os) as id, r.customName as name, os.description as description")
    List<OrganizationServiceQueryResult> getOrganizationServiceByOrgIdAndServiceIds(Long organizationId, List<Long> serviceId);

    @Query("MATCH (os:OrganizationService{imported:false})-[r:"+LINK_WITH_EXTERNAL_SERVICE+"]->(es:OrganizationService{hasMapped:true}) where id(es)={0}  delete r ")
    OrganizationExternalServiceRelationship removeOrganizationExternalServiceRelationship(Long organizationId);

    @Query("MATCH (unit:Unit)-[r:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) where id(unit) IN {0}" +
            "RETURN collect(id(os)) as servicesId ")
    OrganizationServicesAndLevelQueryResult getOrganizationServiceIdsByOrganizationId(List<Long> unitIds);

    @Query("MATCH (organizationService:OrganizationService{isEnabled:true}) where id(organizationService) IN {0}" +
            " return organizationService")
    List<OrganizationService> findAllOrganizationServicesByIds(List<Long> organizationServicesIds);

/*created by bobby
* */
    //TODO add country check for result
    @Query(" MATCH  (o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(o) In {0} " +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) " +
            " RETURN {children: case when os  is NULL then [] else collect({id:id(ss),name:ss.name,description:ss.description}) END, id:id(os),name:os.name,description:os.description} as result ")
    List<Map<String,Object>> getOrgServicesByOrgSubTypesIds(Set<Long> organizationSubTypeIds);
}
