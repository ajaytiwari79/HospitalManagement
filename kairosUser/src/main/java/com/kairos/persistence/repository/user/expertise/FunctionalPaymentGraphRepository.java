package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentMatrixQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface FunctionalPaymentGraphRepository extends Neo4jBaseRepository<FunctionalPayment, Long> {
    @Query("match(functionalPayment:FunctionalPayment{deleted:false})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise:Expertise{deleted:false}) where id(expertise)={0}" +
            " return id(functionalPayment) as id, functionalPayment.name as name,functionalPayment.startDate as startDate,functionalPayment.endDate as endDate,functionalPayment.published as published, " +
            " functionalPayment.paidOutFrequency as paidOutFrequency ORDER BY startDate ASC")
    List<FunctionalPaymentDTO> getFunctionalPaymentOfExpertise(Long expertiseId);

    @Query("match(functionalPayment:FunctionalPayment{deleted:false})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise:Expertise{deleted:false}) where id(expertise)={0}" +
            "return functionalPayment ORDER BY functionalPayment.startDate DESC LIMIT 1")
    FunctionalPayment getLastFunctionalPaymentOfExpertise(Long expertiseId);

    @Query(" match(functionalPayment:FunctionalPayment) where id(functionalPayment)={0}\n" +
            " match(functionalPayment)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(functionalPaymentMatrix:FunctionalPaymentMatrix)\n" +
            " match(functionalPaymentMatrix)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            " match(functionalPaymentMatrix)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(seniorityLevelFunction:SeniorityLevelFunction)-[:FOR_SENIORITY_LEVEL]-(seniorityLevel:SeniorityLevel) " +
            " match(seniorityLevelFunction)-[function_amt:" + HAS_FUNCTIONAL_AMOUNT + "]-(function:Function) \n" +
            "with functionalPaymentMatrix ,seniorityLevel,seniorityLevelFunction,pga,collect({functionId:id(function),amount:function_amt.amount}) as functions \n" +
            "with functionalPaymentMatrix ,functions,seniorityLevelFunction,seniorityLevel,collect (id(pga)) as payGroupAreaIds \n" +
            "with functionalPaymentMatrix ,payGroupAreaIds,collect ({seniorityLevelId:id(seniorityLevel),functions:functions}) as seniorityLevelFunctions \n" +
            " return id(functionalPaymentMatrix) as id,payGroupAreaIds as payGroupAreasIds ,seniorityLevelFunctions as seniorityLevelFunction ORDER BY functionalPaymentMatrix.creationDate")
    List<FunctionalPaymentMatrixQueryResult> getFunctionalPaymentMatrix(Long functionalPaymentId);
}
