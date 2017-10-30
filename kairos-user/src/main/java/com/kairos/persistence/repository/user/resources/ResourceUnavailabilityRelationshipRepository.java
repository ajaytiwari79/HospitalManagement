package com.kairos.persistence.repository.user.resources;

import com.kairos.persistence.model.user.resources.ResourceUnavailabilityRelationship;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prabjot on 25/10/17.
 */
@Repository
public interface ResourceUnavailabilityRelationshipRepository extends GraphRepository<ResourceUnavailabilityRelationship>{
}
