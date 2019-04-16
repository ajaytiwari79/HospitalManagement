package com.kairos.persistence.repository.user.office_esources_and_metadata;

import com.kairos.persistence.model.user.office_esources_and_metadata.OfficeResources;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by @pankaj on 9/2/17.
 */
@Repository
public interface OfficeResourceRepository extends Neo4jBaseRepository<OfficeResources,Long>{

    @Query("MATCH (o:Organization)-[r:ORGANIZATION_HAS_OFFICE_RESOURCE]->(n:OfficeResources) WHERE id(o)={0} " +
            "RETURN {type:n.resourceType,resources:collect({id:id(n),name:n.name, resourceType:n.resourceType})} as data;")
    List<Map> getOfficeResourcesByOrganizationId(long organizationId);

    @Query("MATCH(o:Organization)-[r:ORGANIZATION_HAS_OFFICE_RESOURCE]->(n:OfficeResources) WHERE id(o)={0} RETURN n;")
    List<OfficeResources> getByOrganization(long organization);
}
