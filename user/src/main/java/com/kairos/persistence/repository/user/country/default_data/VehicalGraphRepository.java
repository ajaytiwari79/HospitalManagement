package com.kairos.persistence.repository.user.country.default_data;

import com.kairos.persistence.model.user.resources.Vehicle;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicalGraphRepository extends Neo4jBaseRepository<Vehicle,Long> {
}
