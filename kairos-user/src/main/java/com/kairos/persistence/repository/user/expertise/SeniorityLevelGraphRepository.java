package com.kairos.persistence.repository.user.expertise;

import com.kairos.persistence.model.user.expertise.FunctionAndSeniorityLevelQueryResult;
import com.kairos.persistence.model.user.expertise.SeniorityLevel;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_FUNCTION;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PAY_GROUP_AREA;

/**
 * Created by vipul on 29/3/18.
 */
@Repository
public interface SeniorityLevelGraphRepository extends Neo4jBaseRepository<SeniorityLevel, Long> {
    @Query("match(s:SeniorityLevel) where id(s)={0}\n" +
            "optional match(s)-[rel:" + HAS_FUNCTION + "]-(fun:Function)\n" +
            "optional match(s)-[:" + HAS_PAY_GROUP_AREA + "]-(pga:PayGroupArea)\n" +
            "return case when fun IS NOT NULL then collect(distinct{functionId:id(fun),amount:rel.amount})else [] end as functions,collect(DISTINCT pga) as payGroupAreas")
    FunctionAndSeniorityLevelQueryResult getFunctionAndPayGroupAreaBySeniorityLevelId(Long seniorityLevelId);

}
