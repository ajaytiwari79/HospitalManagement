package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.enums.MasterDataTypeEnum;
import com.kairos.persistence.model.user.country.feature.Feature;
import com.kairos.persistence.model.user.country.feature.FeatureQueryResult;
import com.kairos.persistence.model.user.country.tag.Tag;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY_HAS_FEATURE;
import static com.kairos.persistence.model.constants.RelationshipConstants.COUNTRY_HAS_TAG;

/**
 * Created by prerna on 4/12/17.
 */
@Repository
public interface FeatureGraphRepository extends GraphRepository<Feature>{

    List<Feature> findAll();

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE feature.name={0} AND id(country) = {1} AND feature.deleted={2} \n" +
            "RETURN CASE WHEN count(feature)>0 THEN true ELSE false END")
    boolean isFeatureExistsWithSameName(String name, Long countryId, boolean isDeleted);


    @Query("MATCH (c:Country) WHERE id(c)={0} CREATE (c)-[:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature{name:{1}, description:{2}, deleted:false, creationDate:{3}, lastModificationDate:{3}}) return feature")
    Feature createFeature(Long countryId, String featureName, String description, Long date);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(feature)={0} AND id(country) = {1} AND feature.deleted={2} \n" +
            "RETURN feature")
    Feature getFeatureById(Long tagId, Long countryId, boolean isDeleted);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(feature)={0} AND id(country) = {1} \n" +
            "SET feature.name={2},feature.description={3}, feature.lastModificationDate={4}\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    FeatureQueryResult updateFeature(Long featureId, Long countryId, String name, String description, long lastModificationDate);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(country)={0} AND feature.deleted= {1} AND lower(feature.name) contains lower({2})\n" +
            "return id(feature) as id, feature.name as name, feature.description as description")
    List<FeatureQueryResult> getListOfFeatures(Long countryId , boolean deleted, String searchTextRegex);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n" +
            "WHERE id(country)={0} AND feature.deleted= {1} AND id(feature) IN {2}\n" +
            "return feature")
    List<Feature> getListOfFeaturesByIds(Long countryId , boolean deleted, List<Long> featureIds);

    @Query("Match (country:Country)-[r:"+COUNTRY_HAS_FEATURE+"]->(feature:Feature)\n"+
            "WHERE id(country)={0} AND feature.name = {1} AND feature.deleted= {2} return feature")
    Feature getFeatureByName(long countryId, String name, boolean deleted);

}
