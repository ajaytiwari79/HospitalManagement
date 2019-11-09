package com.kairos.persistence.repository.user.employment;

import com.kairos.persistence.model.user.employment.EmploymentLineFunctionRelationShip;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.APPLICABLE_FUNCTION;

public interface EmploymentLineFunctionRelationShipGraphRepository extends Neo4jBaseRepository<EmploymentLineFunctionRelationShip, Long> {

    @Query("MATCH(empLine:EmploymentLine)-[rel:"+APPLICABLE_FUNCTION+"]->(function:Function) WHERE id(empLine)={0} RETURN empLine,COLLECT(rel),COLLECT(function) ")
    List<EmploymentLineFunctionRelationShip> findAllByEmploymentLineId(Long employmentLineId);

    @Query("MATCH(empLine:EmploymentLine)-[rel:"+APPLICABLE_FUNCTION+"]->(function:Function) WHERE id(empLine)={0} RETURN empLine,COLLECT(rel),COLLECT(function) ")
    void linkExistingFunction(Long existingEmploymentLineId,Long employmentLineIdToBeLinked);
}
