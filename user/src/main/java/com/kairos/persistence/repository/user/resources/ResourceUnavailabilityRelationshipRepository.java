package com.kairos.persistence.repository.user.resources;
import com.kairos.persistence.model.user.resources.ResourceUnavailabilityRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 25/10/17.
 */
@Repository
public interface ResourceUnavailabilityRelationshipRepository extends Neo4jBaseRepository<ResourceUnavailabilityRelationship,Long> {
}
