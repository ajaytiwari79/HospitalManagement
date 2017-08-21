package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.VatType;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface VatTypeGraphRepository extends GraphRepository<VatType>{

    List<VatType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(vt:VatType {isEnabled:true}) where id(c)={0} return {id:id(vt), name:vt.name, description:vt.description,percentage:vt.percentage,code:vt.code } as result")
    List<Map<String,Object>> findVATtypeByCountry(long countryId);
}
