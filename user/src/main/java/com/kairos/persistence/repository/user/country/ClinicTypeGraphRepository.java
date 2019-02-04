package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.ClinicType;
import com.kairos.persistence.model.country.default_data.ClinicTypeDTO;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;
import java.util.Map;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * Created by oodles on 9/1/17.
 */
public interface ClinicTypeGraphRepository extends Neo4jBaseRepository<ClinicType,Long>{
    @Query("MATCH (c:Country)-[:"+BELONGS_TO+"]-(ct:ClinicType {isEnabled:true}) where id(c)={0} " +
            "RETURN id(ct) as id, ct.name as name, ct.description as description ORDER BY ct.creationDate ASC")
    List<ClinicTypeDTO> findClinicByCountryId(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(clinicType:ClinicType {isEnabled:true}) WHERE id(country)={0} AND id(clinicType)<>{2} AND clinicType.name =~{1}  " +
            " WITH count(clinicType) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean clinicTypeExistInCountryByName(Long countryId, String name, Long currentClinicType);
}
