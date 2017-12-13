package com.kairos.persistence.repository.user.resources;


import com.kairos.persistence.model.user.resources.VehicleLocation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 13/12/17.
 */
@Repository
public interface VehicleLocationRepository extends GraphRepository<VehicleLocation> {


    @Query("MATCH (vl:VehicleLocation) where vl.enabled= true return vl")
    List<VehicleLocation> findAll();
}


