package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.user.unit_position.EmploymentFunctionRelationship;
import com.kairos.persistence.model.user.unit_position.EmploymentFunctionRelationshipQueryResult;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface EmploymentFunctionRelationshipRepository extends Neo4jBaseRepository<EmploymentFunctionRelationship, Long> {

    @Query("MATCH (employment:Employment)  where id(employment) = {0} \n" +
            "MATCH (function:Function) where id(function)={1}\n" +
            "MERGE(employment)-[rel:APPLIED_FUNCTION ]->(function)\n" +
            "ON CREATE set rel.appliedDates = {2} " +
            "ON MATCH set rel.appliedDates = rel.appliedDates+{2} ")
    void createEmploymentFunctionRelationship(Long employmentId, Long functionId, List<String> localDate);


    @Query("MATCH (employment:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(employment) = {0} AND {1} in rel.appliedDates WITH rel,function,\n" +
            "FILTER (x IN rel.appliedDates WHERE x <> {1}) as filteredDates\n" +
            "SET rel.appliedDates = filteredDates RETURN id(function)")
    Long removeDateFromEmploymentFunctionRelationship(Long employmentId, String localDate);

    @Query("MATCH (employment:Employment{deleted:false}) ,(function:Function{deleted:false})  where id(employment) = {0} AND id(function) IN {1} " +
            "MATCH(employment)-[rel:APPLIED_FUNCTION]->(function) RETURN id(rel) as id, function as function,employment as employment,rel.appliedDates as appliedDates ")
    List<EmploymentFunctionRelationshipQueryResult> findAllByFunctionIdAndEmploymentId(Long employmentId, Set<Long> functionIds);

    @Query("MATCH (employment:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(employment) = {0} AND any(x IN rel.appliedDates WHERE x IN {1}) RETURN id(rel) as id, function as function,employment as employment,rel.appliedDates as appliedDates")
    List<EmploymentFunctionRelationshipQueryResult> findAllByAppliedDatesIn(Long employmentId, Set<String> appliedDates);

    @Query("MATCH (employment:Employment)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(employment) = {0} AND {1} in rel.appliedDates  RETURN id(function)" )
    Long getApplicableFunction(Long employmentId, String localDate);

    @Query("MATCH (employment:Employment{deleted:false})-[rel:APPLIED_FUNCTION ]->(function:Function{deleted:false}) where id(employment) in {0} RETURN id(rel) as id,rel.appliedDates as appliedDates,employment as employment,function as function  " )
    List<EmploymentFunctionRelationshipQueryResult> getApplicableFunctionIdWithDatesByEmploymentIds(Set<Long> employmentIds);

    @Query("MATCH (employment:Employment{deleted:false})-[rel:APPLIED_FUNCTION ]->(function:Function{deleted:false})  where id(employment) in {0} RETURN employment as employment,function as function ,id(rel) as id, rel.appliedDates as appliedDates ")
    List<EmploymentFunctionRelationshipQueryResult> getApplicableFunctionsWithRelationShipIByEmploymentId(Set<Long> employmentIds);
}
