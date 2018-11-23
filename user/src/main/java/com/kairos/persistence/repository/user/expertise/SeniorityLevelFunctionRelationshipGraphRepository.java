package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
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
           "({2} IS NULL AND (fp.endDateMillis IS NULL OR fp.endDateMillis > {1}))\n" +
           "OR \n" +
           "({2} IS NOT NULL AND  ({1} < fp.endDateMillis OR {2}>fp.startDateMillis))\n" +
           "return fp")
   List<FunctionalPayment> findAllActiveByPayTableId(Long payTableId,Long startDate,Long endDate);
}
