package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.KairosStatus;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface KairosStatusGraphRepository extends GraphRepository<KairosStatus>{

    List<KairosStatus> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ks:KairosStatus {isEnabled:true}) where id(c)={0} return {id:id(ks), name:ks.name, description:ks.description } as result")
    List<Map<String,Object>> findKairosStatusByCountry(long countryId);
}
