package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.country.FunctionDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_ORGANIZATION_LEVEL;

/**
 * Created by pavan on 13/3/18.
 */
@Repository
public interface FunctionGraphRepository extends Neo4jBaseRepository<Function,Long> {

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} " +
            "OPTIONAL MATCH(function)-[:HAS_ORGANIZATION_LEVEL]->(level:Level) " +
            "OPTIONAL MATCH(function)-[:HAS_UNION]->(union:Organization{union:true}) " +
            "with country,function, collect(DISTINCT level) as organizationLevels, collect(DISTINCT union) as unions   return id(function) as id,function.name as name,function.description as description," +
            "function.startDate as startDate,function.endDate as endDate,unions,organizationLevels")
    List<FunctionDTO> findFunctionsByCountry(long countryId);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} return id(function) as id,function.name as name")
    List<FunctionDTO> findFunctionsIdAndNameByCountry(long countryId);


    @Query("MATCH (c:Country)-[:BELONGS_TO]-(fun:Function{deleted:false}) where id(c)={0} AND LOWER(fun.name)=LOWER({1}) return fun")
    Function findByNameIgnoreCase(Long countryId,String name);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} AND id(function) <> {1} AND LOWER(function.name)=LOWER({2}) return function")
    Function findByNameExcludingCurrent(Long countryId,Long functionId,String name);

    @Query("MATCH(fun:Function{deleted:false}) where id(fun) IN {0} return fun")
    List<Function> findAllFunctionsById(Set<Long> functionIds);

    @Query("MATCH (level:Level)<-[:"+HAS_ORGANIZATION_LEVEL+"]-(function:Function{deleted:false}) where id(level)={0} return id(function) as id,function.name as name")
    List<FunctionDTO> getFunctionsByOrganizationLevel(Long organizationLevelId);

}
