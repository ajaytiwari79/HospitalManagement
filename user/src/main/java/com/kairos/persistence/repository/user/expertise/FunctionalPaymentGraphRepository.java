package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentMatrixQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface FunctionalPaymentGraphRepository extends Neo4jBaseRepository<FunctionalPayment, Long> {
    @Query("MATCH(functionalPayment:FunctionalPayment{deleted:false,hasDraftCopy:false})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise:Expertise{deleted:false}) WHERE id(expertise)={0}" +
            " RETURN id(functionalPayment) as id,functionalPayment.startDate as startDate,functionalPayment.endDate as endDate,functionalPayment.published as published, " +
            " functionalPayment.paymentUnit as paymentUnit ORDER BY startDate ASC")
    List<FunctionalPaymentDTO> getFunctionalPaymentOfExpertise(Long expertiseId);

    @Query("MATCH(functionalPayment:FunctionalPayment{deleted:false})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise:Expertise{deleted:false}) WHERE id(expertise)={0}" +
            "RETURN functionalPayment ORDER BY functionalPayment.startDate DESC LIMIT 1")
    FunctionalPayment getLastFunctionalPaymentOfExpertise(Long expertiseId);

    @Query(" MATCH(functionalPayment:FunctionalPayment) WHERE id(functionalPayment)={0}\n" +
            " MATCH(functionalPayment)-[:" + FUNCTIONAL_PAYMENT_MATRIX + "]-(functionalPaymentMatrix:FunctionalPaymentMatrix)\n" +
            " MATCH(functionalPaymentMatrix)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n " +
            " WITH functionalPaymentMatrix ,COLLECT (id(pga)) as payGroupAreaIds \n" +
            " MATCH(functionalPaymentMatrix)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(seniorityLevelFunction:SeniorityLevelFunction)" +
            " MATCH(seniorityLevel:SeniorityLevel)<-[:" + FOR_SENIORITY_LEVEL + "]- (seniorityLevelFunction)-[function_amt:" + HAS_FUNCTIONAL_AMOUNT + "]-(function:Function) " +
            " WITH functionalPaymentMatrix ,seniorityLevel,seniorityLevelFunction,payGroupAreaIds, COLLECT({functionId:id(function),amountEditableAtUnit:function_amt.amountEditableAtUnit, amount:function_amt.amount}) as functions\n" +
            " RETURN id(functionalPaymentMatrix) as id,payGroupAreaIds as payGroupAreasIds ,COLLECT ({seniorityLevelId:id(seniorityLevel),from:seniorityLevel.from,to:seniorityLevel.to,functions:functions}) as seniorityLevelFunction")
    List<FunctionalPaymentMatrixQueryResult> getFunctionalPaymentMatrix(Long functionalPaymentId);


    @Query(" MATCH(functionalPaymentMatrix:FunctionalPaymentMatrix)-[relation:" + HAS_PAY_GROUP_AREA + "]->(pga:PayGroupArea) WHERE id(functionalPaymentMatrix)={0} " +
            " detach delete relation")
    void removeAllPayGroupAreas(Long functionalPaymentMatrixId);

    @Query(" MATCH(functionalPayment:FunctionalPayment{deleted:false})-[relation:" + VERSION_OF + "]->(parentFunctionalPayment:FunctionalPayment{deleted:false}) WHERE id(functionalPayment)={0} " +
            " RETURN id(parentFunctionalPayment) as id, parentFunctionalPayment.name as name,parentFunctionalPayment.startDate as startDate,parentFunctionalPayment.endDate as endDate,parentFunctionalPayment.published as published, " +
            " parentFunctionalPayment.paymentUnit as paymentUnit")
    FunctionalPaymentDTO getParentFunctionalPayment(Long functionalPaymentId);

    @Query("MATCH(childFunctionalPayment:FunctionalPayment{deleted:false})-[relation:VERSION_OF]->(functionalPayment:FunctionalPayment{deleted:false}) \n" +
            "WHERE id(childFunctionalPayment)={0} AND id(functionalPayment)={1}\n" +
            " set functionalPayment.hasDraftCopy=false set functionalPayment.endDate={2} detach delete relation")
    void setEndDateToFunctionalPayment(Long functionalPaymentId, Long parentFunctionalPaymentId, LocalDate endDate);

    @Query("MATCH(parent:Expertise)-[:VERSION_OF]-(child:Expertise) WHERE id(parent)={0} AND id(child)={1}\n" +
            "MATCH(parent)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn:FunctionalPayment)\n" +
            "MERGE (child)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn)")
    void linkFunctionalPaymentExpertise(Long expertiseId, Long newExpertiseId);


    @Query("MATCH(parent:Expertise) WHERE id(parent)={0} \n" +
            "MATCH   (child:Expertise) WHERE id(child)={1} \n" +
            "MATCH(parent)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn:FunctionalPayment)\n" +
            "MERGE (child)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn)")
    void linkFunctionalPaymentInExpertise(Long expertiseId, Long newExpertiseId);

    @Query("MATCH(expertise:Expertise)<-[:"+APPLICABLE_FOR_EXPERTISE+"]-(funPayment:FunctionalPayment)  WHERE id(expertise)={0}\n" +
            "MATCH(funPayment)-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]->(fpm:FunctionalPaymentMatrix)\n" +
            "MATCH(seniorityLevel:SeniorityLevel) WHERE id(seniorityLevel)={1}\n" +
            "MATCH(fpm)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]-(:SeniorityLevelFunction)-[oldRel:"+HAS_FUNCTIONAL_AMOUNT+"]-(function:Function)\n" +
            "WITH seniorityLevel,fpm,oldRel,COLLECT(function) as functions\n" +
            "FOREACH (currentFunction IN (functions)| \n" +
            " CREATE UNIQUE (seniorityLevel)<-[:"+FOR_SENIORITY_LEVEL+"]-(newSL:SeniorityLevelFunction{deleted:false})\n" +
            " CREATE UNIQUE (fpm)-[:"+SENIORITY_LEVEL_FUNCTIONS+"]->(newSL)\n" +
            " CREATE UNIQUE(newSL)-[:"+HAS_FUNCTIONAL_AMOUNT+"{amount:0,amountEditableAtUnit:oldRel.amountEditableAtUnit}]->(currentFunction))")
    void linkWithFunctionPayment(Long expertiseId,Long seniorityLevelId);


}
