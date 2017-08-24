package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.HousingType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface HousingTypeGraphRepository extends GraphRepository<HousingType>{

    List<HousingType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ht:HousingType {isEnabled:true}) where id(c)={0} return {id:id(ht), name:ht.name, description:ht.description } as result")
    List<Map<String,Object>> findHousingTypeByCountry(long countryId);
}
