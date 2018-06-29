package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.user.unit_position.UnitPositionEmploymentTypeRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by vipul on 6/4/18.
 */
@Repository
public interface UnitPositionEmploymentTypeRelationShipGraphRepository extends Neo4jBaseRepository<UnitPositionEmploymentTypeRelationShip, Long> {
}