package com.kairos.persistence.repository.user.country.functions;

import com.kairos.persistence.model.country.functions.Function;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.country.functions.FunctionWithAmountQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by pavan on 13/3/18.
 */
@Repository
public interface FunctionGraphRepository extends Neo4jBaseRepository<Function, Long> {

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(function:Function{deleted:false}) where id(country)={0} " +
            "OPTIONAL MATCH(function)-[:" + HAS_ORGANIZATION_LEVEL + "]->(level:Level) " +
            "OPTIONAL MATCH(function)-[:" + HAS_UNION + "]->(union:Organization{union:true}) " +
            "with country,function, collect(DISTINCT level) as organizationLevels, collect(DISTINCT union) as unions   " +
            "RETURN id(function) as id,function.name as name,function.description as description," +
            "function.startDate as startDate,function.endDate as endDate,unions,organizationLevels,function.icon as icon ORDER BY function.creationDate  DESC")
    List<FunctionDTO> findFunctionsByCountry(long countryId);

    @Query("MATCH (country:Country)-[:" + BELONGS_TO + "]-(function:Function{deleted:false}) where id(country)={0} " +
            "RETURN id(function) as id,function.name as name ORDER BY function.creationDate  DESC")
    List<FunctionDTO> findFunctionsIdAndNameByCountry(long countryId);


    @Query("MATCH (c:Country)-[:" + BELONGS_TO + "]-(fun:Function{deleted:false}) where id(c)={0} AND LOWER(fun.name)=LOWER({1}) RETURN fun")
    Function findByNameIgnoreCase(Long countryId, String name);

    @Query("MATCH (country:Country)-[: " + BELONGS_TO + "]-(function:Function{deleted:false}) where id(country)={0} AND id(function) <> {1} AND LOWER(function.name)=LOWER({2}) " +
            "RETURN function")
    Function findByNameExcludingCurrent(Long countryId, Long functionId, String name);

    @Query("MATCH(function:Function{deleted:false}) where id(function) IN {0} RETURN function")
    List<Function> findAllFunctionsById(Set<Long> functionIds);

    @Query("MATCH (level:Level)<-[:" + HAS_ORGANIZATION_LEVEL + "]-(function:Function{deleted:false}) where id(level)={0} " +
            "RETURN id(function) as id,function.name as name")
    List<FunctionDTO> getFunctionsByOrganizationLevel(Long organizationLevelId);

    @Query("MATCH(expertise:Expertise{deleted:false,published:true}) where id(expertise)={0}\n" +
            "MATCH(expertise)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(:FunctionalPayment)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(fpm:FunctionalPaymentMatrix) \n" +
            "MATCH(fpm)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(slf:SeniorityLevelFunction) " +
            "MATCH(slf)-[:" + HAS_FUNCTIONAL_AMOUNT + "]-(fn:Function) \n" +
            "RETURN distinct id(fn) as id ,fn.name as name")
    List<FunctionDTO> getFunctionsByExpertiseId(Long expertiseId);

    @Query("MATCH (unit:Unit) where id(unit)={3} \n" +
            "MATCH(unit)-[:" + CONTACT_ADDRESS + "]-(:ContactAddress)-[:" + MUNICIPALITY + "]-(municipality:Municipality)<-[rel:" + HAS_MUNICIPALITY + "]-(payGroupArea:PayGroupArea{deleted:false}) \n" +
            "MATCH(expertise:Expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(sl:SeniorityLevel) WHERE id(sl)={2} AND id(expertise)={0} \n" +
            "MATCH(functionalPayment:FunctionalPayment{deleted:false,published:true})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise) \n" +
            "WHERE  date(functionalPayment.startDate)<=DATE({1}) AND (functionalPayment.endDate IS NULL OR date(functionalPayment.endDate)>=DATE({1}))\n" +
            "MATCH(sl)<-[:" + FOR_SENIORITY_LEVEL + "]-(slf:SeniorityLevelFunction)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(fpm:FunctionalPaymentMatrix)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(functionalPayment) \n" +
            "MATCH (fpm)-[:" + HAS_PAY_GROUP_AREA + "]-(payGroupArea) \n" +
            "with slf,fpm  MATCH(slf)-[rel:" + HAS_FUNCTIONAL_AMOUNT + "]-(function:Function) \n" +
            "RETURN distinct id(function) as id,function.name as name,rel.amount as amount,function.icon as icon,rel.amountEditableAtUnit as amountEditableAtUnit")
    List<FunctionDTO> getFunctionsByExpertiseAndSeniorityLevel(Long expertiseId, String selectedDate, Long seniorityLevelId, Long unitId);


    @Query("MATCH (unit:Unit) where id(unit)={0} \n" +
            "MATCH(unit)-[:" + CONTACT_ADDRESS + "]-(:ContactAddress)-[:" + MUNICIPALITY + "]-(municipality:Municipality)<-[rel:" + HAS_MUNICIPALITY + "]-(payGroupArea:PayGroupArea{deleted:false}) \n" +
            "MATCH(expertise:Expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(sl:SeniorityLevel) WHERE id(sl)={2} AND id(expertise)={1} \n" +
            "MATCH(functionalPayment:FunctionalPayment{deleted:false,published:true})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise) \n" +
            "WHERE  DATE(functionalPayment.startDate)<=DATE({3}) AND (functionalPayment.endDate IS NULL OR date(functionalPayment.endDate)>=DATE({3}))\n" +
            "MATCH(sl)<-[:" + FOR_SENIORITY_LEVEL + "]-(slf:SeniorityLevelFunction)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(fpm:FunctionalPaymentMatrix)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(functionalPayment) \n" +
            "MATCH (fpm)-[:" + HAS_PAY_GROUP_AREA + "]->(payGroupArea) \n" +
            "with slf,fpm  MATCH(slf)-[rel:" + HAS_FUNCTIONAL_AMOUNT + "]-(function:Function) WHERE ID(function) IN {4} \n" +
            "RETURN distinct function as function,rel.amount as amount,rel.amountEditableAtUnit as amountEditableAtUnit")
    List<FunctionWithAmountQueryResult> getFunctionsByExpertiseAndSeniorityLevelAndIds(Long unitId, Long expertiseId, Long seniorityLevelId, String selectedDate, List<Long> functions);

    @Query("MATCH(o:Unit)<-[:"+IN_UNIT+"]-(employment:Employment{deleted:false})-[rel:APPLIED_FUNCTION]->(appliedFunction:Function) where id(o)={0} AND " +
            "({2} IS NULL AND (employment.endDate IS NULL OR date(employment.endDate) > DATE({1})))\n" +
            "OR \n" +
            "(DATE({2}) IS NOT NULL AND  (DATE({1}) < date(employment.endDate) OR DATE({2})>date(employment.startDate)))\n" +
            "WITH employment,CASE WHEN appliedFunction IS NULL THEN [] ELSE Collect({id:id(appliedFunction),name:appliedFunction.name,icon:appliedFunction.icon,appliedDates:rel.appliedDates}) end as appliedFunctions\n" +
            " RETURN  id(employment) as id , appliedFunctions as appliedFunctions")
    List<EmploymentQueryResult> findAppliedFunctionsAtEmpployment(Long unitId, String startDate, String endDate);

    @Query("MATCH(unit:Unit)<-[:IN_UNIT]-(employment:Employment)-[rel:APPLIED_FUNCTION]-(function:Function) \n" +
            "WHERE id(unit) = {0} AND id(function) IN {1} \n" +
            "WITH filter(x IN collect(rel.appliedDates) WHERE size(x)>0) as dates \n" +
            "UNWIND dates AS appliedDates WITH  DISTINCT appliedDates \n" +
            "RETURN appliedDates")
    List<Date> findAllDateByFunctionIds(Long unitId, List<Long> functionIds);
}
