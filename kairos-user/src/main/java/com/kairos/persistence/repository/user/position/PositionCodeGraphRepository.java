package com.kairos.persistence.repository.user.position;


import com.kairos.controller.position.UnitEmploymentPositionController;
import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;


import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_POSITION_NAME;

/**
 * Created by pawanmandhan on 27/7/17.
 */


public interface PositionCodeGraphRepository extends Neo4jBaseRepository<PositionCode,Long> {


    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionCode{ isDeleted:true }) WHERE id(o)={0} AND pn.name=~ {1} return pn ")
    PositionCode checkDuplicatePositionCode(long orgId, String positionName);

    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionCode{ isDeleted:true }) WHERE id(o)={0} AND id(pn)= {1} return pn ")
    PositionCode getPositionCodeByUnitIdAndId(long orgId, long positionNameId);


}
