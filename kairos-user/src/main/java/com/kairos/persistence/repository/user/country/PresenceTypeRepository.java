package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.PresenceType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by vipul on 10/11/17.
 */
@Repository
public interface PresenceTypeRepository extends GraphRepository<PresenceType> {
    @Query("match(presenceType:PresenceType)-[:"+BELONGS_TO+"]-> (country:Country) where Id(country)={2} AND presenceType.name =~ {0} AND presenceType.deleted={1} return presenceType")
    PresenceType findByNameAndDeletedAndCountryId(String name, boolean disabled , Long countryId);
}
