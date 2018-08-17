package com.kairos.persistence.repository.user.resources;

import com.kairos.persistence.model.user.resources.ResourceUnAvailability;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceUnAvailabilityGraphRepository extends Neo4jBaseRepository<ResourceUnAvailability,Long> {
}
