package com.kairos.persistence.repository.user.country;

import java.util.List;
import java.util.Map;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.country.ContractType;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface ContractTypeGraphRepository extends GraphRepository<ContractType>{

    @Override
    @Query("MATCH (ct:ContractType {isEnabled:true}) return ct")
    List<ContractType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ct:ContractType {isEnabled:true}) where id(c)={0} return {id:id(ct), name:ct.name, description:ct.description, code:ct.code } as result")
    List<Map<String,Object>> findContractTypeByCountry(long countryId);
}
