package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.LocationType;
import com.kairos.persistence.model.country.default_data.LocationTypeDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface LocationTypeGraphRepository extends Neo4jBaseRepository<LocationType,Long>{

    List<LocationType> findAll();

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(locationType:LocationType {isEnabled:true}) where id(country)={0} " +
            "RETURN id(locationType) as id, locationType.name as name, locationType.description as description ORDER BY locationType.creationDate DESC")
    List<LocationTypeDTO> findLocationTypeByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(locationType:LocationType {isEnabled:true}) WHERE id(country)={0} AND id(locationType)<>{2} AND locationType.name =~{1}  " +
            " WITH count(locationType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean locationTypeExistInCountryByName(Long countryId, String name, Long currentLocationTypeId);

}
