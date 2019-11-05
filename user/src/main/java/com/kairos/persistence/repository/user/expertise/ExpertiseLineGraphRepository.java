package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertiseLineGraphRepository extends Neo4jBaseRepository<ExpertiseLine,Long> {

    @Query("MATCH(exl:ExpertiseLine)-[r:FOR_SENIORITY_LEVEL]->(sl:SeniorityLevel) WHERE ID(exl)={0} return exl,collect(r),collect(sl)")
    ExpertiseLine findOneByLineId(Long id);
}
