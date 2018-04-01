package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionAndSeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.model.user.pay_table.FutureDate;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import com.kairos.response.dto.web.experties.SeniorityLevelDTO;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.FOR_SENIORITY_LEVEL;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

/**
 * Created by vipul on 29/3/18.
 */
@Repository
public interface SeniorityLevelGraphRepository extends Neo4jBaseRepository<SeniorityLevel, Long> {
    @Query("match(s:SeniorityLevel) where id(s)={0}\n" +
            "optional match(s)-[rel:" + HAS_FUNCTION + "]-(function:Function)\n" +
            "optional match(s)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            "return case when function IS NOT NULL then collect(distinct{functionId:id(function),name:function.name ,description:function.description," +
            "startDate:function.startDate ,endDate:function.endDate,amount:rel.amount})else [] end as functions,collect(DISTINCT pga) as payGroupAreas")
    FunctionAndSeniorityLevelQueryResult getFunctionAndPayGroupAreaBySeniorityLevelId(Long seniorityLevelId);

    @Query("match(s:SeniorityLevel) where id(s)={0}\n" +
            "match(s)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea) detach delete rel")
    void removeAllPreviousPayGroupAreaFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(s:SeniorityLevel) where id(s)={0}\n" +
            "match(s)-[rel:" + HAS_FUNCTION + "]-(function:Function) detach delete rel\n")
    void removeAllPreviousFunctionsFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) \n" +
            "optional match(seniorityLevel)-[rel:" + HAS_FUNCTION + "]->(functions:Function) \n" +
            "with expertise,seniorityLevel,CASE when functions IS NULL THEN [] ELSE collect({name:functions.name,id:id(functions),amount:rel.amount }) END as functionData  \n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)  \n" +
            "with expertise,seniorityLevel,functionData,CASE when pga IS NULL THEN [] ELSE  collect  (distinct{name:pga.name,id:id(pga)}) END as payGroupAreas    \n" +
            "return  CASE when seniorityLevel IS NULL THEN [] ELSE collect({from:seniorityLevel.from,pensionPercentage:seniorityLevel.pensionPercentage,freeChoicePercentage:seniorityLevel.freeChoicePercentage," +
            "freeChoiceToPension:seniorityLevel.freeChoiceToPension, to:seniorityLevel.to,basePayGrade:seniorityLevel.basePayGrade,moreThan:seniorityLevel.moreThan,functions:functionData,payGroupAreas:payGroupAreas})  END  as seniorityLevel")
    List<SeniorityLevelDTO> getSeniorityLevelByExpertiseId(Long expertiseId);


}
