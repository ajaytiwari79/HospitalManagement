package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.SeniorityLevelFunctionsRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by vipul on 28/3/18.
 */
@Repository
public interface SeniorityLevelFunctionRelationshipGraphRepository extends Neo4jBaseRepository<SeniorityLevelFunctionsRelationship,Long>{

}
