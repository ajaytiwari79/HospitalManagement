package com.kairos.persistence.repository.user.unit_position;

import com.kairos.persistence.model.user.unit_position.UnitPositionFunctionRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLIED_FUNCTION;

/**
 * Created by oodles on 5/6/18.
 */
@Repository
public interface UnitPositionFunctionRelationshipRepository  extends Neo4jBaseRepository<UnitPositionFunctionRelationship, Long> {

    @Query("MATCH (unitPosition:UnitPosition)-[rel:APPLIED_FUNCTION]->(function:Function) where id(unitPosition) = {0} AND id(function)={1} AND {2} IN rel.appliedDates  \n" +
            "with unitPosition, function, count(rel.appliedDates) as dateCount \n" +
            "return case when dateCount > 0 then true else false end as result")
    Boolean getUnitPositionFunctionRelationshipByUnitPositionAndFunction(Long unitPositionId, Long functionId, Long localDate);

    @Query("MATCH (unitPosition:UnitPosition)  where id(unitPosition) = {0} \n" +
            "match (function:Function) where id(function)={1}\n" +
            "MERGE(unitPosition)-[rel:APPLIED_FUNCTION ]->(function)\n" +
            "ON CREATE set rel.appliedDates = {2} " +
            "ON MATCH set rel.appliedDates = rel.appliedDates+{2} ")
    void createUnitPositionFunctionRelationship(Long unitPositionId, Long functionId, List<Long> localDate);

    /*@Query("MATCH (unitPosition:UnitPosition)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(unitPosition) = {0} AND id(function)={1}\n" +
            "set rel.date= rel.date + {2}")
    void addDateToUnitPositionFunctionRelationship(Long unitPositionId, Long functionId, Long localDate);*/

    @Query("MATCH (unitPosition:UnitPosition)-[rel:APPLIED_FUNCTION ]->(function:Function) where id(unitPosition) = {0} AND {1} in rel.appliedDates with rel,function,\n" +
            "FILTER (x IN rel.appliedDates WHERE x <> {1}) as filteredDates\n" +
            "SET rel.appliedDates = filteredDates return id(function)")
    Long removeDateFromUnitPositionFunctionRelationship(Long unitPositionId, Long localDate);

}
