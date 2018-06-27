package com.kairos.persistence.repository.user.country;

import com.kairos.user.country.CountryAccessGroupRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prerna on 5/3/18.
 */
@Repository
public interface CountryAccessGroupRelationshipRepository extends Neo4jBaseRepository<CountryAccessGroupRelationship, Long> {
}
