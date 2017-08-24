package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OrganizationService;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ORGANIZATION_SERVICES;
import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_SUB_SERVICE;
import static com.kairos.persistence.model.constants.RelationshipConstants.PROVIDE_SERVICE;


/**
 * Created by oodles on 16/9/16.
 */
@Repository
public interface OrganizationServiceRepository extends GraphRepository<OrganizationService>{
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

    OrganizationService findByName(String name);


    @Query("MATCH (c:Country)-[:"+HAS_ORGANIZATION_SERVICES+"]->(os:OrganizationService) WHERE id(c)={0} AND os.name=~ {1} return os ")
    OrganizationService checkDuplicateService(long countryId, String name);

    @Query("MATCH (c:Country)-[:"+HAS_ORGANIZATION_SERVICES+"]->(os:OrganizationService) WHERE id(c)={0} AND os.name=~ {1} return os ")
    List<OrganizationService> getByServiceName(long countryId, String name);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService) WHERE id(os)={0} AND ss.name=~ {1} return ss")
    OrganizationService checkDuplicateSubService(Long id, String name);

    @Query("MATCH (os:OrganizationService) where id(os) ={0} return {id:id(os) ,name:os.name } as result ")
    List<Map<String,Object>> findOneById(Long subServiceId);

    @Query("MATCH (o:Organization)-[:"+PROVIDE_SERVICE+"{isEnabled:true}]->(os:OrganizationService{isEnabled:true}) where id(o)={0}  return id(os) ")
    List<Long> getServiceIdsByOrgId(Long organizationId);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService) WHERE os.name=~ {0} AND ss.name=~ {1} return ss")
    OrganizationService checkDuplicateSubServiceByName(String serviceName, String subServiceName);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService) WHERE id(os)={0} AND ss.name= {1} return ss")
    OrganizationService checkDuplicateSubServiceWithSpecialCharacters(Long id, String name);

    OrganizationService findByKmdExternalId(String kmdExternalId);
}
