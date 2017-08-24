package com.kairos.persistence.repository.user.resources;

/**
 * Created by oodles on 17/10/16.
 */

import com.kairos.persistence.model.user.resources.Resource;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * Interface for CRUD operation on Resources
 */
@Repository
public interface ResourceGraphRepository extends GraphRepository<Resource> {

    /**
     * @return List of Resource
     */
    @Query("MATCH (r:Resource) where s.isEnabled= true return s")
    List<Resource> findAll();

    @Query(" MATCH (o:Organization)-[:ORGANIZATION_HAS_RESOURCE]->(r:Resource) WHERE  id(o)={0} WITH r as res " +
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

    @Query("MATCH(o:Organization)-[:ORGANIZATION_HAS_RESOURCE]->(r:Resource)  where id(o)={0} return r")
    List<Resource> getByUnitId(Long organizationId);
}
