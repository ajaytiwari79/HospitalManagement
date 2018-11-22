package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.SeniorityLevelFunctionsRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by vipul on 28/3/18.
 */
@Repository
public interface SeniorityLevelFunctionRelationshipGraphRepository extends Neo4jBaseRepository<SeniorityLevelFunctionsRelationship,Long>{


   @Query("MATCH(p:PayTable)-[:"+HAS_PAY_GRADE+"]-(payGrade:PayGrade)<-[:"+HAS_BASE_PAY_GRADE+"]-(sl:SeniorityLevel)<-[:"+FOR_SENIORITY_LEVEL+"]-(slf:SeniorityLevelFunction)-[rel:"+HAS_FUNCTIONAL_AMOUNT+"]-(f:Function)\n" +
           "MATCH(slf)<-[:"+SENIORITY_LEVEL_FUNCTIONS+"]-(fpm:FunctionalPaymentMatrix)<-[:"+FUNCTIONAL_PAYMENT_MATRIX+"]-(fp:FunctionalPayment)\n" +
           "WHERE id(p)={0} AND  \n" +
           "(p.endDateMillis IS NULL AND (fp.endDateMillis IS NULL OR fp.endDateMillis > p.startDateMillis))\n" +
           "OR \n" +
           "(p.endDateMillis IS NOT NULL AND  (p.startDateMillis < fp.endDateMillis OR p.endDateMillis>fp.startDateMillis))\n" +
           "return id(rel) as id,rel.amount as amount, rel.amountEditableAtUnit as amountEditableAtUnit,seniorityLevelFunction,function")
   List<SeniorityLevelFunctionsRelationship> findAllActiveByTableId(Long payTableId);
}
