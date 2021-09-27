package com.kairos.persistence.repository.organization;

import com.kairos.persistence.model.organization.OpeningHours;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenningHourGraphRepository extends Neo4jBaseRepository<OpeningHours,Long> {
}
