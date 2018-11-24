package com.kairos.persistence.repository.user.country.functions;

import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
            "with country,function, collect(DISTINCT level) as organizationLevels, collect(DISTINCT union) as unions   RETURN id(function) as id,function.name as name,function.description as description," +
            "function.startDate as startDate,function.endDate as endDate,unions,organizationLevels,function.icon as icon")
    List<FunctionDTO> findFunctionsByCountry(long countryId);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} RETURN id(function) as id,function.name as name")
    List<FunctionDTO> findFunctionsIdAndNameByCountry(long countryId);


    @Query("MATCH (c:Country)-[:BELONGS_TO]-(fun:Function{deleted:false}) where id(c)={0} AND LOWER(fun.name)=LOWER({1}) RETURN fun")
    Function findByNameIgnoreCase(Long countryId, String name);

    @Query("MATCH (country:Country)-[:BELONGS_TO]-(function:Function{deleted:false}) where id(country)={0} AND id(function) <> {1} AND LOWER(function.name)=LOWER({2}) RETURN function")
    Function findByNameExcludingCurrent(Long countryId, Long functionId, String name);

    @Query("MATCH(function:Function{deleted:false}) where id(function) IN {0} RETURN function")
    List<Function> findAllFunctionsById(Set<Long> functionIds);

    @Query("MATCH (level:Level)<-[:" + HAS_ORGANIZATION_LEVEL + "]-(function:Function{deleted:false}) where id(level)={0} RETURN id(function) as id,function.name as name")
    List<FunctionDTO> getFunctionsByOrganizationLevel(Long organizationLevelId);

    @Query("MATCH(expertise:Expertise{deleted:false,published:true}) where id(expertise)={0}\n" +
            "MATCH(expertise)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(:FunctionalPayment)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(fpm:FunctionalPaymentMatrix) \n" +
            "MATCH(fpm)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(slf:SeniorityLevelFunction) " +
            "MATCH(slf)-[:" + HAS_FUNCTIONAL_AMOUNT + "]-(fn:Function) \n" +
            "RETURN distinct id(fn) as id ,fn.name as name")
    List<FunctionDTO> getFunctionsByExpertiseId(Long expertiseId);

    @Query("MATCH (unit:Organization) where id(unit)={3} \n" +
            "MATCH(unit)-[:"+CONTACT_ADDRESS+"]-(:ContactAddress)-[:"+MUNICIPALITY+"]-(municipality:Municipality)<-[rel:"+HAS_MUNICIPALITY+"]-(payGroupArea:PayGroupArea{deleted:false}) \n" +
            "MATCH(expertise:Expertise)-[:"+FOR_SENIORITY_LEVEL+"]->(sl:SeniorityLevel) WHERE id(sl)={2} AND id(expertise)={0} \n" +
            "MATCH(functionalPayment:FunctionalPayment{deleted:false,hasDraftCopy:false,published:true})-[:"+APPLICABLE_FOR_EXPERTISE+"]->(expertise) \n" +
            "where  functionalPayment.startDate<={1} AND (functionalPayment.endDate IS NULL OR functionalPayment.endDate>={1})\n" +
            "MATCH(sl)<-[:"+FOR_SENIORITY_LEVEL+"]-(slf:SeniorityLevelFunction)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]-(fpm:FunctionalPaymentMatrix)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]-(functionalPayment) \n" +
            "with slf,fpm  MATCH(slf)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function:Function) \n" +
            " MATCH (fpm)-[:"+HAS_PAY_GROUP_AREA+"]-(payGroupArea) \n" +
            "RETURN distinct id(function) as id,function.name as name,rel.amount as amount,function.icon as icon,rel.amountEditableAtUnit as amountEditableAtUnit")
    List<FunctionDTO> getFunctionsByExpertiseAndSeniorityLevel(Long expertiseId,LocalDate selectedDate,Long seniorityLevelId,Long unitId);


    @Query("MATCH (unit:Organization) where id(unit)={0} \n" +
            "MATCH(unit)-[:"+CONTACT_ADDRESS+"]-(:ContactAddress)-[:"+MUNICIPALITY+"]-(municipality:Municipality)<-[rel:"+HAS_MUNICIPALITY+"]-(payGroupArea:PayGroupArea{deleted:false}) \n" +
            "MATCH(expertise:Expertise)-[:"+FOR_SENIORITY_LEVEL+"]->(sl:SeniorityLevel) WHERE id(sl)={2} AND id(expertise)={1} \n" +
            "MATCH(functionalPayment:FunctionalPayment{deleted:false,hasDraftCopy:false,published:true})-[:"+APPLICABLE_FOR_EXPERTISE+"]->(expertise) \n" +
            "where  functionalPayment.startDate<={3} AND (functionalPayment.endDate IS NULL OR functionalPayment.endDate>={3})\n" +
            "MATCH(sl)<-[:"+FOR_SENIORITY_LEVEL+"]-(slf:SeniorityLevelFunction)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]-(fpm:FunctionalPaymentMatrix)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]-(functionalPayment) \n" +
            "with slf,fpm  MATCH(slf)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function:Function) WHERE ID(function) IN {4} \n" +
            " MATCH (fpm)-[:"+HAS_PAY_GROUP_AREA+"]-(payGroupArea) \n" +
            "RETURN distinct function as function,rel.amount as amount,rel.amountEditableAtUnit as amountEditableAtUnit")
    List<FunctionWithAmountQueryResult> getFunctionsByExpertiseAndSeniorityLevelAndIds(Long unitId, Long expertiseId, Long seniorityLevelId, LocalDate selectedDate, List<Long> functions);
}
