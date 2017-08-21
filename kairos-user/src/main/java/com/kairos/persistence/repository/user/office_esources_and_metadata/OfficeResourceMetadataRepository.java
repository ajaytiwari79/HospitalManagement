package com.kairos.persistence.repository.user.office_esources_and_metadata;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.office_esources_and_metadata.OfficeResourceTypeMetadata;

/**
 * Created by @pankaj on 9/2/17.
 */
@Repository
public interface OfficeResourceMetadataRepository extends GraphRepository<OfficeResourceTypeMetadata>{

    @Query("MATCH (n:OfficeResourceTypeMetadata) RETURN n LIMIT 1")
    OfficeResourceTypeMetadata getFirstNode();
}
