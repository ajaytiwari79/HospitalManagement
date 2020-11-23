package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.HousingType;
import com.kairos.persistence.model.country.default_data.HousingTypeDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface HousingTypeGraphRepository extends Neo4jBaseRepository<HousingType,Long>{

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(housingType:HousingType {isEnabled:true}) where id(country)={0} " +
            "RETURN housingType")
    List<HousingTypeDTO> findHousingTypeByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(housingType:HousingType {isEnabled:true}) WHERE id(country)={0} AND id(housingType)<>{2} AND housingType.name =~{1}  " +
            " WITH count(housingType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean housingTypeExistInCountryByName(Long countryId, String name, Long currentHousingTypeId);
}
