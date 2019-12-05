package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertiseLineGraphRepository extends Neo4jBaseRepository<ExpertiseLine, Long> {
}
