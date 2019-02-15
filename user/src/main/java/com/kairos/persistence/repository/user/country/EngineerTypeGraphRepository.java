package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.EngineerType;
import com.kairos.persistence.model.country.default_data.EngineerTypeDTO;
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

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(engineerType:EngineerType {isEnabled:true}) where id(country)={0} " +
            "RETURN id(engineerType) as id, engineerType.name as name, engineerType.description as description ORDER BY engineerType.creationDate DESC")
    List<EngineerTypeDTO> findEngineerTypeByCountry(long countryId);

    // Get Engineer Type data for filters by countryId
    @Query("MATCH (et:EngineerType{isEnabled:true})-[:"+BELONGS_TO +"]->(c:Country) where id(c)={0} return toString(id(et)) as id, et.name as value" )
    List<FilterSelectionQueryResult> getEngineerTypeByCountryIdForFilters(Long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(engineerType:EngineerType {isEnabled:true}) WHERE id(country)={0} AND id(engineerType)<>{2} AND engineerType.name =~{1}  " +
            " WITH count(engineerType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean engineerTypeExistInCountryByName(Long countryId, String name, Long currentEngineerTypeId);


}
