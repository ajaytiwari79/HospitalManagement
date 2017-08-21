package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.EmployeeLimit;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface EmployeeLimitGraphRepository extends GraphRepository<EmployeeLimit>{

    List<EmployeeLimit> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(el:EmployeeLimit {isEnabled:true}) where id(c)={0} return {id:id(el), name:el.name, description:el.description,minimum:el.minimum,maximum:el.maximum} as result")
    List<Map<String,Object>> findContractTypeByCountry(long countryId);
}
