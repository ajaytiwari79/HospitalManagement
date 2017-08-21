package com.kairos.persistence.repository.user.integration;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.integration.Twillio;

/**
 * Created by oodles on 21/2/17.
 */
@Repository
public interface TwillioGraphRepository extends GraphRepository<Twillio> {
    Twillio findByOrganizationId(Long organizationId);
}
