package com.kairos.persistence.repository.user.office_esources_and_metadata;
import com.kairos.persistence.model.user.office_esources_and_metadata.OfficeResourceTypeMetadata;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by @pankaj on 9/2/17.
 */
@Repository
public interface OfficeResourceMetadataRepository extends GraphRepository<OfficeResourceTypeMetadata>{

    @Query("MATCH (n:OfficeResourceTypeMetadata) RETURN n LIMIT 1")
    OfficeResourceTypeMetadata getFirstNode();
}
