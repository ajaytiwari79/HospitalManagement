package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.country.default_data.ClinicType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
public interface ClinicTypeGraphRepository extends Neo4jBaseRepository<ClinicType,Long>{
    List<ClinicType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ct:ClinicType {isEnabled:true}) where id(c)={0} return {id:id(ct), name:ct.name, description:ct.description } as result ")
    List<Map<String,Object>> findClinicByCountryId(long countryId);
}
