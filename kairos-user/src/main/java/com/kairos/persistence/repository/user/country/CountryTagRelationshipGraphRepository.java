package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.tag.CountryTagRelationship;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by prerna on 13/11/17.
 */
@Repository
public interface CountryTagRelationshipGraphRepository extends GraphRepository<CountryTagRelationship> {

}
