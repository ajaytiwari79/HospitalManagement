package com.kairos.persistence.repository.user.country;

import com.kairos.persistence.model.user.country.Function;
import com.kairos.persistence.model.user.country.FunctionDTO;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pavan on 13/3/18.
 */
@Repository
public interface FunctionGraphRepository extends Neo4jBaseRepository<Function, Long> {

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} " +
            "OPTIONAL MATCH(function)-[:HAS_ORGANIZATION_LEVEL]->(level:Level) " +
            "OPTIONAL MATCH(function)-[:HAS_UNION]->(union:Organization{union:true}) " +
            "with country,function, collect(DISTINCT level) as organizationLevels, collect(DISTINCT union) as unions   return id(function) as id,function.name as name,function.description as description," +
            "function.startDate as startDate,function.endDate as endDate,unions,organizationLevels,function.icon as icon")
    List<FunctionDTO> findFunctionsByCountry(long countryId);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} return id(function) as id,function.name as name")
    List<FunctionDTO> findFunctionsIdAndNameByCountry(long countryId);


    @Query("MATCH (c:Country)-[:BELONGS_TO]-(fun:Function{deleted:false}) where id(c)={0} AND LOWER(fun.name)=LOWER({1}) return fun")
    Function findByNameIgnoreCase(Long countryId, String name);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} AND id(function) <> {1} AND LOWER(function.name)=LOWER({2}) return function")
    Function findByNameExcludingCurrent(Long countryId, Long functionId, String name);

    @Query("MATCH(function:Function{deleted:false}) where id(function) IN {0} return function")
    List<Function> findAllFunctionsById(Set<Long> functionIds);

    @Query("MATCH (level:Level)<-[:" + HAS_ORGANIZATION_LEVEL + "]-(function:Function{deleted:false}) where id(level)={0} return id(function) as id,function.name as name")
    List<FunctionDTO> getFunctionsByOrganizationLevel(Long organizationLevelId);

    @Query("match(expertise:Expertise{deleted:false,published:true}) where id(expertise)={0}\n" +
            "match(expertise)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(:FunctionalPayment)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(fpm:FunctionalPaymentMatrix) \n" +
            "match(fpm)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(slf:SeniorityLevelFunction) match(slf)-[:" + HAS_FUNCTIONAL_AMOUNT + "]-(fn:Function) \n" +
            "return distinct id(fn) as id ,fn.name as name")
    List<FunctionDTO> getFunctionsByExpertiseId(Long expertiseId);


    @Query("match(e:Expertise) where id(e)={0} \n" +
            "match(e)-[:FOR_SENIORITY_LEVEL]->(sl:SeniorityLevel) where id(sl)={1} \n" +
            "optional match(sl)<-[:FOR_SENIORITY_LEVEL]-(slf:SeniorityLevelFunction) \n" +
            "optional match(slf)-[:HAS_FUNCTIONAL_AMOUNT]-(function:Function) \n" +
            "return id(function) as id,function.name as name")
    List<FunctionDTO> getFunctionsByExpertiseAndSeniorityLevel(Long expertiseId,Long seniorityLevelId);
}
