package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.pay_table.PayGrade;
import com.kairos.persistence.model.user.expertise.Response.SeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 29/3/18.
 */
@Repository
public interface SeniorityLevelGraphRepository extends Neo4jBaseRepository<SeniorityLevel, Long> {
    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade)" +
            "return payGrade as payGrade")
    PayGrade getPayGradeBySeniorityLevelId(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel: " + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) detach delete rel")
    void removePreviousPayGradeFromSeniorityLevel(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel)={0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade)" +
            "return id(seniorityLevel) as id,seniorityLevel.from as from,seniorityLevel.pensionPercentage as pensionPercentage,seniorityLevel.freeChoicePercentage as freeChoicePercentage," +
            "seniorityLevel.freeChoiceToPension as freeChoiceToPension,seniorityLevel.to as to,payGrade as payGrade")
    SeniorityLevelQueryResult getSeniorityLevelById(Long seniorityLevelId);

    @Query("match(seniorityLevel:SeniorityLevel{deleted:false}) where id(seniorityLevel) IN {0}\n" +
            "return seniorityLevel")
    List<SeniorityLevel> findAll(Set<Long> seniorityLevelIds);

    @Query("match(seniorityLevel:SeniorityLevel) where id(seniorityLevel) IN {0}\n" +
            "match(seniorityLevel)-[rel:" + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) " +
            "with CASE when count(payGrade) <> size({0}) then false else true END  as response return response")
    boolean checkPayGradesInSeniorityLevel(List<Long> seniorityLevelIds);


    @Query("match(expertise:Expertise) where id(expertise)={0} \n" +
            "match(expertise)-[:" + FOR_SENIORITY_LEVEL + "]->(seniorityLevel:SeniorityLevel) \n" +
            "match(seniorityLevel)-[rel: " + HAS_BASE_PAY_GRADE + "]->(payGrade:PayGrade) detach delete rel")
    void unlinkAllPayGradesFromExpertise(Long expertiseId);

}
