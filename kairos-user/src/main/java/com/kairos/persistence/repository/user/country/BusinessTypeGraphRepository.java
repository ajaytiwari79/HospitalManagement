package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.BusinessType;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface BusinessTypeGraphRepository extends GraphRepository<BusinessType>{

    List<BusinessType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(bt:BusinessType {isEnabled:true}) where id(c)={0} return {id:id(bt), name:bt.name, description:bt.description } as result")
    List<Map<String,Object>> findBusinesTypeByCountry(long countryId);

    @Query("MATCH (businessType:BusinessType) where id(businessType) in {0} return businessType")
    List<BusinessType> findByIdIn(List<Long> ids);

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(bt:BusinessType {isEnabled:true}) where id(c)={0} return {id:id(bt), name:bt.name, description:bt.description } as result")
    List<Map<String,Object>> findBusinesTypes(long countryId);
}
