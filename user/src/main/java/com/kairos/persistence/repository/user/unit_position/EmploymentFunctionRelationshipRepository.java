package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.user.unit_position.EmploymentFunctionRelationship;
import com.kairos.persistence.model.user.unit_position.EmploymentFunctionRelationshipQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLIED_FUNCTION;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface EmploymentFunctionRelationshipRepository extends Neo4jBaseRepository<EmploymentFunctionRelationship, Long> {

    @Query("MATCH (unitPosition:Employment)  where id(unitPosition) = {0} \n" +
            "MATCH (function:Function) where id(function)={1}\n" +
            "MERGE(unitPosition)-[rel:APPLIED_FUNCTION ]->(function)\n" +
            "ON CREATE set rel.appliedDates = {2} " +
            "ON MATCH set rel.appliedDates = rel.appliedDates+{2} ")
    void createUnitPositionFunctionRelationship(Long unitPositionId, Long functionId, List<String> localDate);


    @Query("MATCH (unitPosition:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(unitPosition) = {0} AND {1} in rel.appliedDates WITH rel,function,\n" +
            "FILTER (x IN rel.appliedDates WHERE x <> {1}) as filteredDates\n" +
            "SET rel.appliedDates = filteredDates RETURN id(function)")
    Long removeDateFromUnitPositionFunctionRelationship(Long unitPositionId, String localDate);

    @Query("MATCH (unitPosition:Employment{deleted:false}) ,(function:Function{deleted:false})  where id(unitPosition) = {0} AND id(function) IN {1} " +
            "MATCH(unitPosition)-[rel:APPLIED_FUNCTION]->(function) RETURN id(rel) as id, function as function,unitPosition as unitPosition,rel.appliedDates as appliedDates ")
    List<EmploymentFunctionRelationshipQueryResult> findAllByFunctionIdAndUnitPositionId(Long unitPositionId, Set<Long> functionIds);

    @Query("MATCH (unitPosition:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(unitPosition) = {0} AND any(x IN rel.appliedDates WHERE x IN {1}) RETURN id(rel) as id, function as function,unitPosition as unitPosition,rel.appliedDates as appliedDates")
    List<EmploymentFunctionRelationshipQueryResult> findAllByAppliedDatesIn(Long unitPositionId, Set<String> appliedDates);

    @Query("MATCH (unitPosition:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(unitPosition) = {0} AND {1} in rel.appliedDates  RETURN id(function)" )
    Long getApplicableFunction(Long unitPositionId, String localDate);

    @Query("MATCH (unitPosition:Employment{deleted:false})-[rel:APPLIED_FUNCTION ]->(function:Function{deleted:false}) where id(unitPosition) in {0} RETURN id(rel) as id,rel.appliedDates as appliedDates,unitPosition as unitPosition,function as function  " )
    List<EmploymentFunctionRelationshipQueryResult> getApplicableFunctionIdWithDatesByEmploymentIds(Set<Long> unitPositionIds);

    @Query("MATCH (unitPosition:Employment{deleted:false})-[rel:APPLIED_FUNCTION ]->(function:Function{deleted:false})  where id(unitPosition) in {0} RETURN unitPosition as unitPosition,function as function ,id(rel) as id, rel.appliedDates as appliedDates ")
    List<EmploymentFunctionRelationshipQueryResult> getApplicableFunctionsWithRelationShipIByEmploymentId(Set<Long> unitPositionIds);
}
