package com.kairos.persistence.repository.user.resources;


import com.kairos.persistence.model.user.resources.VehicleLocation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 13/12/17.
 */
@Repository
public interface VehicleLocationRepository extends Neo4jBaseRepository<VehicleLocation,Long> {


    @Query("MATCH (vl:VehicleLocation) where vl.enabled= true return vl")
    List<VehicleLocation> findAll();
}


