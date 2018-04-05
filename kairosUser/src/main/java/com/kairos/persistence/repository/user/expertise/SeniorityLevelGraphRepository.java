package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionAndSeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 29/3/18.
 */
@Repository
public interface SeniorityLevelGraphRepository extends Neo4jBaseRepository<SeniorityLevel, Long> {
    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade)" +
            "optional match(seniorityLevel)-[rel:" + HAS_FUNCTION + "]-(function:Function)\n" +
            "optional match(seniorityLevel)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            "return case when function IS NOT NULL then collect(distinct{functionId:id(function),name:function.name ,description:function.description," +
            "startDate:function.startDate ,endDate:function.endDate,amount:rel.amount})else [] end as functions,payGrade as payGrade,collect(DISTINCT pga) as payGroupAreas")
    FunctionAndSeniorityLevelQueryResult getFunctionAndPayGroupAreaBySeniorityLevelId(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea) detach delete rel")
    void removeAllPreviousPayGroupAreaFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel: " + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) detach delete rel")
    void removePreviousPayGradeFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_FUNCTION + "]-(function:Function) detach delete rel\n")
    void removeAllPreviousFunctionsFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) where id(expertise)={0} AND id(seniorityLevel)<>{1} \n" +
            "match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGradeData:PayGrade) where id(payGradeData)={2}  " +
            "with count(payGradeData) as payGradeDataCount  " +
            "RETURN case when payGradeDataCount>0 THEN  true ELSE false END as response")
    Boolean checkPayGradeInSeniorityLevel(Long expertiseId, Long currentSeniorityLevelId, Long payGradeId);

}
