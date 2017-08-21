package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import com.kairos.persistence.model.user.country.ClinicType;

/**
 * Created by oodles on 9/1/17.
 */
public interface ClinicTypeGraphRepository extends GraphRepository<ClinicType>{
    List<ClinicType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ct:ClinicType {isEnabled:true}) where id(c)={0} return {id:id(ct), name:ct.name, description:ct.description } as result ")
    List<Map<String,Object>> findClinicByCountryId(long countryId);
}
