package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.IndustryType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface IndustryTypeGraphRepository extends GraphRepository<IndustryType>{

    List<IndustryType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(it:IndustryType {isEnabled:true}) where id(c)={0} return {id:id(it), name:it.name, description:it.description } as result")
    List<Map<String,Object>> findIndustryTypeByCountry(long countryId);
}
