package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.CitizenStatus;

/**
 * Created by oodles on 5/1/17.
 */
@Repository
public interface CitizenStatusGraphRepository extends GraphRepository<CitizenStatus>{

    @Query("MATCH (cs:CitizenStatus{isEnabled:true})-[:CIVILIAN_STATUS]-(c:Country) where id(c)={0} return {id:id(cs), name:cs.name,description:cs.description} as result")
    List<Map<String,Object>> findCitizenStatusByCountryId(long countryId);

    @Query("MATCH (cs:CitizenStatus{isEnabled:true})-[:CIVILIAN_STATUS]-(c:Country) where id(c)={0} return {value:id(cs), label:cs.name,description:cs.description} as result")
    List<Map<String,Object>> findCitizenStatusByCountryIdAnotherFormat(long countryId);

    CitizenStatus findByName(String name);

    @Query("MATCH (cs:CitizenStatus{isEnabled:true})-[:CIVILIAN_STATUS]-(c:Country) where id(c)={0} AND cs.description={1} return cs")
    CitizenStatus findByDescription(long countryId, String description);
}
