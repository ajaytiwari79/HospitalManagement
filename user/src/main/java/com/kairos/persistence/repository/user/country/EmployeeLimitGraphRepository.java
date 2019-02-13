package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.BusinessTypeDTO;
import com.kairos.persistence.model.country.default_data.EmployeeLimit;
import com.kairos.persistence.model.country.default_data.EmployeeLimitDTO;
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
public interface EmployeeLimitGraphRepository extends Neo4jBaseRepository<EmployeeLimit,Long>{

    List<EmployeeLimit> findAll();

    @Query("MATCH (country:Country)<-[:"+ BELONGS_TO +"]-(employeeLimit:EmployeeLimit {isEnabled:true}) where id(country)={0} " +
            "RETURN id(employeeLimit) as id, employeeLimit.name as name, employeeLimit.description as description, employeeLimit.minimum as minimum, employeeLimit.maximum as maximum  ORDER BY employeeLimit.creationDate DESC")
    List<EmployeeLimitDTO> findEmployeeLimitByCountry(long countryId);

    @Query("MATCH(country:Country)<-[:" + BELONGS_TO + "]-(employeeLimit:EmployeeLimit {isEnabled:true}) WHERE id(country)={0} AND id(employeeLimit)<>{2} AND employeeLimit.name =~{1}  " +
            " WITH count(employeeLimit) as totalCount " +
            " RETURN CASE WHEN totalCount>0 THEN TRUE ELSE FALSE END as result")
    Boolean employeeLimitExistInCountryByName(Long countryId, String name, Long currentEmployeeLimitId);
}
