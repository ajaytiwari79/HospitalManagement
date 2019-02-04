package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.IndustryType;
import com.kairos.persistence.model.country.default_data.IndustryTypeDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface IndustryTypeGraphRepository extends Neo4jBaseRepository<IndustryType,Long>{

    @Query("MATCH (country:Country)<-[:"+BELONGS_TO+"]-(it:IndustryType {isEnabled:true}) WHERE id(country)={0} " +
            "RETURN id(it) as id, it.name as name, it.description as description ORDER BY it.creationDate ASC")
    List<IndustryTypeDTO> findIndustryTypeByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(industryType:IndustryType {isEnabled:true}) WHERE id(country)={0} AND id(industryType)<>{2} AND industryType.name =~{1}  " +
            " WITH count(industryType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean industryTypeExistInCountryByName(Long countryId, String name, Long currentIndustryTypeId);
}
