package com.kairos.persistence.repository.user.region;

import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelGraphRepository extends Neo4jBaseRepository<Level,Long> {
}
