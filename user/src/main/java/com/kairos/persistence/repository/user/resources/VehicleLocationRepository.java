package com.kairos.persistence.repository.user.resources;


import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.persistence.model.user.resources.VehicleLocationDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 13/12/17.
 */
@Repository
public interface VehicleLocationRepository extends Neo4jBaseRepository<VehicleLocation,Long> {

    @Query("MATCH(vehicleLocation:VehicleLocation{enabled:true}) " +
            "RETURN id(vehicleLocation) as id, vehicleLocation.name as name, vehicleLocation.description as description ORDER BY vehicleLocation.creationDate DESC")
    List<VehicleLocationDTO> getVehicleLocation();

    @Query("MATCH(vehicleLocation:VehicleLocation{enabled:true}) WHERE id(vehicleLocation)<>{1} AND vehicleLocation.name =~{0}  " +
            " WITH count(vehicleLocation) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean vehicleLocationExistByName(String name, Long currentVehicleLocationId);
}


