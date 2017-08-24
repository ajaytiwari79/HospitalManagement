package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.user.country.EngineerType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface EngineerTypeGraphRepository extends GraphRepository<EngineerType>{

    @Override
    @Query("Match (engineerType:EngineerType{isEnabled:true}) return engineerType")
    List<EngineerType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(et:EngineerType {isEnabled:true}) where id(c)={0} return et")
    List<EngineerType> findEngineerTypeByCountry(long countryId);

}
