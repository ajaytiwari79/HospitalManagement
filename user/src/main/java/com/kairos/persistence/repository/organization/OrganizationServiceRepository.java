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
import java.util.Optional;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by oodles on 16/9/16.
 */
@Repository
public interface OrganizationServiceRepository extends Neo4jBaseRepository<OrganizationService,Long>{
    List<OrganizationService> findAll();

//    @Query("MATCH (c:Country),(n:OrganizationService{isEnabled:true}) where id(c)={0} match (c)-[:HAS_ORGANIZATION_SERVICES]->(n) OPTIONAL MATCH (n)-[:ORGANIZATION_SUB_SERVICE]->(s:OrganizationService{isEnabled:true})" +
//            " RETURN {children: case when s is NULL then [] else collect({id:id(s),name:s.name,description:s.description}) END,id:id(n),name:n.name,description:n.description} as result")
//    List<Map<String,Object>> getOrganizationServicesByCountryId(long countryId);

    @Query("MATCH (c:Country)-[:HAS_ORGANIZATION_SERVICES]->(n:OrganizationService{isEnabled:true}) where id(c)={0} WITH DISTINCT n" +
            " OPTIONAL MATCH (n)-[r:ORGANIZATION_SUB_SERVICE]->(s:OrganizationService{isEnabled:true}) RETURN DISTINCT  n,COLLECT(s),COLLECT(r)")
    List<OrganizationService> getOrganizationServicesByCountryId(long countryId);

    @Query("MATCH (c:Country)-[:HAS_ORGANIZATION_SERVICES]->(n:OrganizationService{isEnabled:true}) where id(c)={0} WITH DISTINCT n" +
            "  RETURN id(n)")
    Set<Long> getOrganizationServicesIdsByCountryId(long countryId);

    @Query(" MATCH  (o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(o)={0} " +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) " +
            " RETURN {" +
            "translations:{english :{name: CASE WHEN os.`translatedNames.english` IS NULL THEN '' ELSE os.`translatedNames.english` END, description : CASE WHEN os.`translatedDescriptions.english` IS NULL THEN '' ELSE os.`translatedDescriptions.english` END},\n" +
            "hindi:{name: CASE WHEN os.`translatedNames.hindi` IS NULL THEN '' ELSE os.`translatedNames.hindi` END, description : CASE WHEN os.`translatedDescriptions.hindi` IS NULL THEN '' ELSE os.`translatedDescriptions.hindi` END},\n" +
            "danish:{name: CASE WHEN os.`translatedNames.danish` IS NULL THEN '' ELSE os.`translatedNames.danish` END, description : CASE WHEN os.`translatedDescriptions.danish` IS NULL THEN '' ELSE os.`translatedDescriptions.danish` END},\n" +
            "britishenglish:{name: CASE WHEN os.`translatedNames.britishenglish` IS NULL THEN '' ELSE os.`translatedNames.britishenglish` END, description : CASE WHEN os.`translatedDescriptions.britishenglish` IS NULL THEN '' ELSE os.`translatedDescriptions.britishenglish` END}},\n" +
            "children: case when os  is NULL then [] else collect({" +
            "translations:{english :{name: CASE WHEN ss.`translatedNames.english` IS NULL THEN '' ELSE ss.`translatedNames.english` END, description : CASE WHEN ss.`translatedDescriptions.english` IS NULL THEN '' ELSE ss.`translatedDescriptions.english` END},\n" +
            "hindi:{name: CASE WHEN ss.`translatedNames.hindi` IS NULL THEN '' ELSE ss.`translatedNames.hindi` END, description : CASE WHEN ss.`translatedDescriptions.hindi` IS NULL THEN '' ELSE ss.`translatedDescriptions.hindi` END},\n" +
            "danish:{name: CASE WHEN ss.`translatedNames.danish` IS NULL THEN '' ELSE ss.`translatedNames.danish` END, description : CASE WHEN ss.`translatedDescriptions.danish` IS NULL THEN '' ELSE ss.`translatedDescriptions.danish` END},\n" +
            "britishenglish:{name: CASE WHEN ss.`translatedNames.britishenglish` IS NULL THEN '' ELSE ss.`translatedNames.britishenglish` END, description : CASE WHEN ss.`translatedDescriptions.britishenglish` IS NULL THEN '' ELSE ss.`translatedDescriptions.britishenglish` END}},\n" +
            "id:id(ss),name:ss.name,description:ss.description}) END, id:id(os),name:os.name,description:os.description} as result ")
    List<Map<String,Object>> getOrgServicesByOrgType(long organizationType);

    @Override
    OrganizationService findOne(Long aLong);

    @Query("MATCH (c:Country)-[:"+HAS_ORGANIZATION_SERVICES+"]->(os:OrganizationService{isEnabled:true}) WHERE id(c)={0} AND os.name=~ {1} AND id(os)<>{2} return CASE WHEN COUNT(os)>0 THEN TRUE ELSE FALSE END AS result ")
    boolean checkDuplicateService(long countryId, String name,Long id);

    @Query("MATCH (os:OrganizationService)-[:"+ORGANIZATION_SUB_SERVICE+"]->(ss:OrganizationService{isEnabled:true}) WHERE id(os)={0} AND ss.name=~ {1} return ss")
    OrganizationService checkDuplicateSubService(Long id, String name);


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

    @Query("MATCH(exl:ExpertiseLine)  WHERE id(exl)={0} " +
            "OPTIONAL MATCH(exl)-[rel:"+SUPPORTS_SERVICES+"]-(os:OrganizationService)\n" +
            "with exl,rel  " +
            "MATCH(newService:OrganizationService) where id(newService) IN {1} \n" +
            "DETACH delete rel \n" +
            "CREATE UNIQUE(exl)-[:"+SUPPORTS_SERVICES+"]-(newService) ")
    void addServices(Long expertiseLineId, List<Long> newServicesTobeLinked);



/*created by bobby
* */
    //TODO add country check for result
    @Query(" MATCH  (o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(o) In {0} " +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) " +
            " RETURN {children: case when os  is NULL then [] else collect({id:id(ss),name:ss.name,description:ss.description}) END, id:id(os),name:os.name,description:os.description} as result ")
    List<Map<String,Object>> getOrgServicesByOrgSubTypesIds(Set<Long> organizationSubTypeIds);

    @Query("MATCH  (unit:Organization)-[:SUB_TYPE_OF]-(o:OrganizationType)-[:ORGANIZATION_TYPE_HAS_SERVICES]->(ss:OrganizationService{isEnabled:true}) where id(unit) = {0} \n" +
            "MATCH (ss)<-[:ORGANIZATION_SUB_SERVICE]-(os:OrganizationService {isEnabled:true} ) \n" +
            "WITH case when os  is NULL then [] else collect({id:id(ss),name:ss.name}) END as organizationSubServices,os\n" +
            "RETURN  id(os) as id ,os.name as name,organizationSubServices as organizationSubServices")
    List<OrganizationServiceQueryResult> getAllOrganizationServicesByUnitId(Long unitId);
}
