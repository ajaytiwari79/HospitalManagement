package com.kairos.persistence.repository.organization;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.region.LocalAreaTag;

/**
 * Created by neuron on 12/6/17.
 */

@Repository
public interface OrganizationMetadataRepository extends GraphRepository<LocalAreaTag> {

    @Query("MATCH (org:Organization)-[:HAS_LOCAL_AREA_TAGS]->(lat:LocalAreaTag) where id(org)={0} AND lat.isDeleted=false with lat,org\n" +
            "MATCH (lat)-[:LAT_AND_LNG]->(latlng:LatLng) with latlng, lat return {id:id(lat), name:lat.name, paths:collect({lat:latlng.lat, lng:latlng.lng}) } as tags")
    List<Map<String, Object>> findAllByIsDeletedAndUnitId(long unitId);
}