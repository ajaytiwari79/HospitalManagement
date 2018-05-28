package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.FunctionalPaymentMatrix;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentMatrixQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@Repository
public interface FunctionalPaymentGraphRepository extends Neo4jBaseRepository<FunctionalPayment, Long> {
    @Query("match(functionalPayment:FunctionalPayment{deleted:false,hasDraftCopy:false})-[:" + APPLICABLE_FOR_EXPERTISE + "]->(expertise:Expertise{deleted:false}) where id(expertise)={0}" +
            " return id(functionalPayment) as id, functionalPayment.name as name,functionalPayment.startDate as startDate,functionalPayment.endDate as endDate,functionalPayment.published as published, " +
            " functionalPayment.paymentUnit as paymentUnit ORDER BY startDate ASC")
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
            "with functionalPaymentMatrix ,payGroupAreaIds,collect ({seniorityLevelId:id(seniorityLevel),from:seniorityLevel.from,to:seniorityLevel.to,functions:functions}) as seniorityLevelFunctions \n" +
            " return id(functionalPaymentMatrix) as id,payGroupAreaIds as payGroupAreasIds ,seniorityLevelFunctions as seniorityLevelFunction ORDER BY functionalPaymentMatrix.creationDate")
    List<FunctionalPaymentMatrixQueryResult> getFunctionalPaymentMatrix(Long functionalPaymentId);


    @Query(" match(functionalPaymentMatrix:FunctionalPaymentMatrix) where id(functionalPaymentMatrix)={0}\n" +
            " match(functionalPaymentMatrix)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            " match(functionalPaymentMatrix)-[:" + SENIORITY_LEVEL_FUNCTIONS + "]-(seniorityLevelFunction:SeniorityLevelFunction)-[:FOR_SENIORITY_LEVEL]-(seniorityLevel:SeniorityLevel) " +
            " match(seniorityLevelFunction)-[function_amt:" + HAS_FUNCTIONAL_AMOUNT + "]-(function:Function) \n" +
            "with functionalPaymentMatrix ,seniorityLevel,seniorityLevelFunction,pga,collect({functionId:id(function),amount:function_amt.amount}) as functions \n" +
            "with functionalPaymentMatrix ,functions,seniorityLevelFunction,seniorityLevel,collect (id(pga)) as payGroupAreaIds \n" +
            "with functionalPaymentMatrix ,payGroupAreaIds,collect ({seniorityLevelId:id(seniorityLevel),from:seniorityLevel.from,to:seniorityLevel.to,functions:functions}) as seniorityLevelFunctions \n" +
            " return id(functionalPaymentMatrix) as id,payGroupAreaIds as payGroupAreasIds ,seniorityLevelFunctions as seniorityLevelFunction ORDER BY functionalPaymentMatrix.creationDate")
    FunctionalPaymentMatrix getFunctionalPaymentMatrixById(Long functionalPaymentMatrixId);

    @Query(" MATCH(functionalPaymentMatrix:FunctionalPaymentMatrix)-[relation:" + HAS_PAY_GROUP_AREA + "]->(pga:PayGroupArea) where id(functionalPaymentMatrix)={0} " +
            " detach delete relation")
    void removeAllPayGroupAreas(Long functionalPaymentMatrixId);

    @Query(" match(functionalPayment:FunctionalPayment{deleted:false})-[relation:" + VERSION_OF + "]->(parentFunctionalPayment:FunctionalPayment{deleted:false}) where id(functionalPayment)={0} " +
            " return id(parentFunctionalPayment) as id, parentFunctionalPayment.name as name,parentFunctionalPayment.startDate as startDate,parentFunctionalPayment.endDate as endDate,parentFunctionalPayment.published as published, " +
            " parentFunctionalPayment.paymentUnit as paymentUnit")
    FunctionalPaymentDTO getParentFunctionalPayment(Long functionalPaymentId);

    @Query("match(childFunctionalPayment:FunctionalPayment{deleted:false})-[relation:VERSION_OF]->(functionalPayment:FunctionalPayment{deleted:false}) \n" +
            "where id(childFunctionalPayment)={0} AND id(functionalPayment)={1}\n" +
            " set functionalPayment.hasDraftCopy=false set functionalPayment.endDate={2} detach delete relation")
    void setEndDateToFunctionalPayment(Long functionalPaymentId, Long parentFunctionalPaymentId, Long endDate);

    @Query("match(parent:Expertise)-[:VERSION_OF]-(child:Expertise) where id(parent)={0} AND id(child)={1}\n" +
            "match(parent)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn:FunctionalPayment)\n" +
            "merge (child)<-[:" + APPLICABLE_FOR_EXPERTISE + "]-(fn)")
    void linkFunctionalPaymentExpertise(Long expertiseId, Long newExpertiseId);
}
