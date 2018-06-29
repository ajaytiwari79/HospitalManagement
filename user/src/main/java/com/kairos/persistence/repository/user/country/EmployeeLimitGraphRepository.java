package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.EmployeeLimit;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface EmployeeLimitGraphRepository extends Neo4jBaseRepository<EmployeeLimit,Long>{

    List<EmployeeLimit> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(el:EmployeeLimit {isEnabled:true}) where id(c)={0} return {id:id(el), name:el.name, description:el.description,minimum:el.minimum,maximum:el.maximum} as result")
    List<Map<String,Object>> findContractTypeByCountry(long countryId);
}
