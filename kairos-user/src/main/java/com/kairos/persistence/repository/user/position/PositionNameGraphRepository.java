package com.kairos.persistence.repository.user.position;


import com.kairos.persistence.model.user.position.PositionName;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_POSITION_NAME;

/**
 * Created by pawanmandhan on 27/7/17.
 */


public interface PositionNameGraphRepository extends GraphRepository<PositionName> {


    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionName{ isEnabled:true }) WHERE id(o)={0} AND pn.name=~ {1} return pn ")
    PositionName checkDuplicatePositionName(long orgId, String positionName);

    @Query("MATCH (o:Organization)-[:"+HAS_POSITION_NAME+"]->(pn:PositionName{ isEnabled:true }) WHERE id(o)={0} AND id(pn)= {1} return pn ")
    PositionName getPositionNameByUnitIdAndId(long orgId, long positionNameId);


}
