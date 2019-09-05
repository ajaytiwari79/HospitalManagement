package com.kairos.persistence.repository.user.office_esources_and_metadata;

import com.kairos.persistence.model.user.office_esources_and_metadata.OfficeResourceTypeMetadata;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by @pankaj on 9/2/17.
 */
@Repository
public interface OfficeResourceMetadataRepository extends Neo4jBaseRepository<OfficeResourceTypeMetadata,Long>{


}
