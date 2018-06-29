package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.KairosStatus;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface KairosStatusGraphRepository extends Neo4jBaseRepository<KairosStatus,Long>{

    List<KairosStatus> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ks:KairosStatus {isEnabled:true}) where id(c)={0} return {id:id(ks), name:ks.name, description:ks.description } as result")
    List<Map<String,Object>> findKairosStatusByCountry(long countryId);
}
