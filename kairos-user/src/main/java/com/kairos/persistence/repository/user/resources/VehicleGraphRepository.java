package com.kairos.persistence.repository.user.resources;

import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

/**
 * Created by prabjot on 13/10/17.
 */
public interface VehicleGraphRepository extends Neo4jBaseRepository<Vehicle,Long>{
}
