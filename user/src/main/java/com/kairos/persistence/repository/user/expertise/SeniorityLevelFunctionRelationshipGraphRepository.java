package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.SeniorityLevelFunctionsRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 28/3/18.
 */
@Repository
public interface SeniorityLevelFunctionRelationshipGraphRepository extends Neo4jBaseRepository<SeniorityLevelFunctionsRelationship,Long>{

}
