package com.kairos.persistence.repository.user.country;

import com.kairos.user.country.HousingType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface HousingTypeGraphRepository extends Neo4jBaseRepository<HousingType,Long>{

    List<HousingType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ht:HousingType {isEnabled:true}) where id(c)={0} return {id:id(ht), name:ht.name, description:ht.description } as result")
    List<Map<String,Object>> findHousingTypeByCountry(long countryId);
}
