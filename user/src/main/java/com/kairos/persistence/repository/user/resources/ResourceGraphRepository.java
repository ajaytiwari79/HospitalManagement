package com.kairos.persistence.repository.user.resources;

/**
 * Created by oodles on 17/10/16.
 */

import com.kairos.persistence.model.user.resources.Resource;
import com.kairos.persistence.model.user.resources.ResourceUnAvailability;
import com.kairos.persistence.model.user.resources.ResourceWrapper;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.ORGANIZATION_HAS_RESOURCE;
import static com.kairos.persistence.model.constants.RelationshipConstants.UNAVAILABLE_ON;


/**
 * Interface for CRUD operation on Resources
 */
@Repository
public interface ResourceGraphRepository extends Neo4jBaseRepository<Resource,Long> {

    /**
     * @return List of Resource
     */
    @Query("MATCH (r:Resource) where s.isEnabled= true return s")
    List<Resource> findAll();

    @Query(" MATCH (o:Organization)-[:ORGANIZATION_HAS_RESOURCE]->(r:Resource{deleted:false}) WHERE  id(o)={0} AND r.deleted=false WITH r as res " +
            "OPTIONAL MATCH (res)-[:RESOURCE_NOT_AVAILABLE_ON]->(ra:ResourceUnAvailability)  RETURN " +
            "{ name:res.name, " +
            " id:id(res), " +
            "registrationNumber:res.registrationNumber, " +
            "number:res.number, " +
            " modelDescription:res.modelDescription, " +
            "costPerKM:res.costPerKM , " +
            "fuelType:res.fuelType ,  " +
            "isEnabled:res.isEnabled ,  " +
            "unavailability:collect({ " +
            "     notAvailableFrom:ra.notAvailableFrom, " +
            "     notAvailableTo:ra.notAvailableTo })  " +
            "} as resourceList ")
    List<Map<String,Object>> getResourceByOrganizationId(Long organizationId);

    /*
    *  Get the list of resources to assigned a beacon
    *  @Param unitID, resource type(need only these types of resource) and resourceId(already assigned resource)
    *  Return the available resources to assigned beacons
    * */
    @Query("MATCH(o:Organization)-[:ORGANIZATION_HAS_RESOURCE]->(r:Resource)  where id(o)={0} AND (r.name IN {1}) AND NOT (id(r) IN {2})  return r")
    List<Resource> getUnassignedResourceWithBeacon(Long unit, String[] resourceTypes, long[] resourceIds);

    @Query("MATCH(o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(r:Resource)  where id(o)={0} AND r.deleted=false  return r")
    List<Resource> getByUnitId(Long organizationId);

    @Query("MATCH (o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(r:Resource{deleted:false})-[:VEHICLE_TYPE]->(vehicle:Vehicle) where id(o)={0}\n" +
            "return id(r) as id,r.registrationNumber as registrationNumber,r.number as number,r.modelDescription as modelDescription,r.costPerKM as costPerKM,r.fuelType as fuelType," +
            "vehicle as vehicleType,r.creationDate as creationDate,r.decommissionDate as decommissionDate,case when r.decommissionDate is null then false else true end as isDecommision")
    List<ResourceWrapper> getResources(Long organizationId);

    @Query("MATCH (o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(r:Resource{deleted:false})-[:VEHICLE_TYPE]->(vehicle:Vehicle) where id(o)={0}\n" +
            "Optional Match (r)-[:"+UNAVAILABLE_ON+"{month:{1},year:{2}}]->(ru:ResourceUnAvailability)\n" +
            "return id(r) as id,r.registrationNumber as registrationNumber,r.number as number,r.modelDescription as modelDescription,r.costPerKM as costPerKM,r.fuelType as fuelType," +
            "vehicle as vehicleType,r.creationDate as creationDate,r.decommissionDate as decommissionDate,case when r.decommissionDate is null then false else true end as isDecommision,collect(ru) as resourceUnAvailabilities")
    List<ResourceWrapper> getResourcesWithUnAvailability(Long organizationId,Integer month,Integer year);

    @Query("Match (resource:Resource)-[r:"+UNAVAILABLE_ON+"]->(ru:ResourceUnAvailability) where id(resource)={0} AND id(ru)={1} detach delete ru")
    void deleteResourceUnavailability(Long resourceId,Long unavailableDateId);

    @Query("Match (resource:Resource)-[:"+UNAVAILABLE_ON+"{month:{1},year:{2}}]->(resourceUnAvailability:ResourceUnAvailability) where id(resource)={0}\n" +
            "return resourceUnAvailability")
    List<ResourceUnAvailability> getResourceUnavailability(Long resourceId, Integer month, Integer year);

    @Query("Match (resource:Resource)-[:"+UNAVAILABLE_ON+"]->(resourceUnAvailability:ResourceUnAvailability) where id(resource)={0} and id(resourceUnAvailability)={1}\n" +
            "return resourceUnAvailability")
    ResourceUnAvailability getResourceUnavailabilityById(Long resourceId, Long unavailabilityId);

    @Query("MATCH(o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(r:Resource{deleted:false})  where id(o)={0} AND r.registrationNumber={1} return r")
    Resource getResourceByRegistrationNumberAndUnit(Long organizationId,String registrationNumber);

    @Query("MATCH(o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(r:Resource{deleted:false})  where id(o)={0} AND id(r)={1} return r")
    Resource getResourceOfOrganizationById(Long organizationId,Long resourceId, boolean deleted);


}
