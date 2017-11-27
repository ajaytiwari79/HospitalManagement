package com.kairos.persistence.repository.user.position;


import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.model.user.position.UnitEmploymentPosition;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_POSITION_NAME;

/**
 * Created by pawanmandhan on 27/7/17.
 */


public interface PositionCodeGraphRepository extends GraphRepository<UnitEmploymentPosition> {


    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionCode{ isDeleted:true }) WHERE id(o)={0} AND pn.name=~ {1} return pn ")
    UnitEmploymentPosition checkDuplicatePositionName(long orgId, String positionName);

    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionCode{ isDeleted:true }) WHERE id(o)={0} AND id(pn)= {1} return pn ")
    UnitEmploymentPosition getPositionCodeByUnitIdAndId(long orgId, long positionNameId);


}
