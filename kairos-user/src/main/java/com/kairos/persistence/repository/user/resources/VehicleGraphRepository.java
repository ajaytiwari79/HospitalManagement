package com.kairos.persistence.repository.user.resources;

import com.kairos.persistence.model.user.resources.Vehicle;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by prabjot on 13/10/17.
 */
public interface VehicleGraphRepository extends GraphRepository<Vehicle>{
}
