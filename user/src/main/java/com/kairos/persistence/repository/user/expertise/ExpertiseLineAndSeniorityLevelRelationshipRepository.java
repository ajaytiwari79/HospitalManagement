package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ExpertiseLineSeniorityLevelRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;

@Repository
public interface ExpertiseLineAndSeniorityLevelRelationshipRepository extends Neo4jBaseRepository<ExpertiseLineSeniorityLevelRelationship, Long> {

    @Query("MATCH(el:ExpertiseLine)-[r:"+FOR_SENIORITY_LEVEL+"]-(sl:SeniorityLevel) RETURN r")
    List<ExpertiseLineSeniorityLevelRelationship> findAllByLineId(Long expertiseLineId);
}
