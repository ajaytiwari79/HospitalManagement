package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.ClinicType;
import com.kairos.persistence.model.country.default_data.ClinicTypeDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
public interface ClinicTypeGraphRepository extends Neo4jBaseRepository<ClinicType,Long>{
    @Query("MATCH (c:Country)-[:"+BELONGS_TO+"]-(ct:ClinicType {isEnabled:true}) where id(c)={0} " +
            "RETURN ct")
    List<ClinicType> findClinicByCountryId(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(clinicType:ClinicType {isEnabled:true}) WHERE id(country)={0} AND id(clinicType)<>{2} AND clinicType.name =~{1}  " +
            " WITH count(clinicType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean clinicTypeExistInCountryByName(Long countryId, String name, Long currentClinicType);
}
