package com.kairos.persistence.repository.user.country;
import com.kairos.persistence.model.country.default_data.ContractType;
import org.springframework.data.neo4j.annotation.Query;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 9/1/17.
 */
@Repository
public interface ContractTypeGraphRepository extends Neo4jBaseRepository<ContractType,Long>{

    @Override
    @Query("MATCH (ct:ContractType {isEnabled:true}) return ct")
    List<ContractType> findAll();

    @Query("MATCH (c:Country)-[:BELONGS_TO]-(ct:ContractType {isEnabled:true}) where id(c)={0} return {id:id(ct), name:ct.name, description:ct.description, code:ct.code } as result")
    List<Map<String,Object>> findContractTypeByCountry(long countryId);
}
