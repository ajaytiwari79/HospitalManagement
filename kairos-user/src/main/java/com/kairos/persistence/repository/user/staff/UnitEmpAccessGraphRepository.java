package com.kairos.persistence.repository.user.staff;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.staff.UnitEmpAccessRelationship;

/**
 * Created by prabjot on 30/1/17.
 */
@Repository
public interface UnitEmpAccessGraphRepository extends GraphRepository<UnitEmpAccessRelationship> {
}
