package com.kairos.persistence.repository.user.country;
import com.kairos.user.country.EngineerType;
import com.kairos.persistence.model.user.filter.FilterSelectionQueryResult;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface EngineerTypeGraphRepository extends Neo4jBaseRepository<EngineerType,Long>{

    @Override
    @Query("Match (engineerType:EngineerType{isEnabled:true}) return engineerType")
    List<EngineerType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(et:EngineerType {isEnabled:true}) where id(c)={0} return et")
    List<EngineerType> findEngineerTypeByCountry(long countryId);

    // Get Engineer Type data for filters by countryId
    @Query("MATCH (et:EngineerType{isEnabled:true})-[:"+BELONGS_TO +"]->(c:Country) where id(c)={0} return toString(id(et)) as id, et.name as value" )
    List<FilterSelectionQueryResult> getEngineerTypeByCountryIdForFilters(Long countryId);


}
