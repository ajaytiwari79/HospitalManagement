package com.kairos.persistence.repository.user.country.default_data;

import com.kairos.persistence.model.country.RelationType;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelationTypeGraphRepository extends Neo4jBaseRepository<RelationType,Long> {
}
