package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.feature.Feature;
import com.kairos.persistence.model.country.feature.FeatureQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prerna on 4/12/17.
 */
@Repository
public interface FeatureGraphRepository extends Neo4jBaseRepository<Feature,Long> {

    List<Feature> findAll();

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE feature.name=~{0} AND id(country) = {1} AND feature.deleted={2} \n" +
            "RETURN CASE WHEN count(feature)>0 THEN true ELSE false END")
    boolean isFeatureExistsWithSameName(String name, Long countryId, boolean isDeleted);


    @Query("MATCH (c:Country) WHERE id(c)={0} CREATE (c)-[:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature{name:{1}, description:{2}, deleted:false, lastModificationDate:{3}}) return feature")
    Feature createFeature(Long countryId, String featureName, String description, String date);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(feature)={0} AND id(country) = {1} AND feature.deleted={2} \n" +
            "RETURN feature")
    Feature getFeatureById(Long tagId, Long countryId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(feature)={0} AND id(country) = {1} \n" +
            "SET feature.name={2},feature.description={3}, feature.lastModificationDate={4}\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    FeatureQueryResult updateFeature(Long featureId, Long countryId, String name, String description, String lastModificationDate);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(country)={0} AND feature.deleted= {1} AND lower(feature.name) contains lower({2})\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    List<FeatureQueryResult> getListOfFeatures(Long countryId , boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(country)={0} AND feature.deleted= {1} AND id(feature) IN {2}\n" +
            "return feature")
    List<Feature> getListOfFeaturesByCountryAndIds(Long countryId , boolean deleted, List<Long> featureIds);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n"+
            "WHERE id(country)={0} AND feature.name = {1} AND feature.deleted= {2} return feature")
    Feature getFeatureByName(long countryId, String name, boolean deleted);

    @Query("MATCH (o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(res:Resource{deleted:false})-[:VEHICLE_TYPE]->(vehicle:Vehicle)-[r:"+VEHICLE_HAS_FEATURE+"]->(feature:Feature{deleted:{2}}) where id(o)={0} AND id(res)={1}\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    List<FeatureQueryResult> getResourcesAvailableFeatures(Long organizationId, Long resourceId, boolean deleted);

    @Query("MATCH (o:Organization)-[:"+ORGANIZATION_HAS_RESOURCE+"]->(res:Resource{deleted:false})-[:"+RESOURCE_HAS_FEATURE+"]->(feature:Feature{deleted:{2}}) where id(o)={0} AND id(res)={1}\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    List<FeatureQueryResult> getResourcesSelectedFeatures(Long organizationId, Long resourceId, boolean deleted);

    @Query("Match (o:Organization)-[:"+COUNTRY+"]->(country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(o)={0} AND feature.deleted= false AND id(feature) IN {3} with feature\n" +
            "MATCH (res:Resource{deleted:{2}})-[:VEHICLE_TYPE]->(vehicle:Vehicle)-[r:"+VEHICLE_HAS_FEATURE+"]->(feature)\n" +
            "WHERE id(res) ={1}\n" +
            "return feature")
    List<Feature> getAvailableFeaturesOfResourceByOrganizationAndIds(Long organizationId , Long resourceId, boolean deleted, List<Long> featureIds);

    @Query("MATCH (resource:Resource)-[r:"+RESOURCE_HAS_FEATURE+"]->(feature:Feature) WHERE id(resource)={0} \n"+
            "DELETE r")
    void detachResourceFeatures(long resourceId);
}
