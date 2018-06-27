package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.OwnershipType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface OwnershipTypeGraphRepository extends Neo4jBaseRepository<OwnershipType,Long>{

    List<OwnershipType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ot:OwnershipType {isEnabled:true}) where id(c)={0} return {id:id(ot), name:ot.name, description:ot.description } as result")
    List<Map<String,Object>> findOwnershipTypeByCountry(long countryId);
}
