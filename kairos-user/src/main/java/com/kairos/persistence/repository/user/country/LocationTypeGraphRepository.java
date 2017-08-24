package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.user.country.LocationType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface LocationTypeGraphRepository extends GraphRepository<LocationType>{

    List<LocationType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(lt:LocationType {isEnabled:true}) where id(c)={0} return {id:id(lt), name:lt.name, description:lt.description } as result")
    List<Map<String,Object>> findLocationTypeByCountry(long countryId);
}
