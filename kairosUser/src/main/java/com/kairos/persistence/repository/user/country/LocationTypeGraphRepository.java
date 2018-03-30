package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.user.country.LocationType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface LocationTypeGraphRepository extends Neo4jBaseRepository<LocationType,Long>{

    List<LocationType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(lt:LocationType {isEnabled:true}) where id(c)={0} return {id:id(lt), name:lt.name, description:lt.description } as result")
    List<Map<String,Object>> findLocationTypeByCountry(long countryId);
}
