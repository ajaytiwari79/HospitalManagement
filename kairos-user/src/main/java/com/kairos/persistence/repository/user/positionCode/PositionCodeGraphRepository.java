package com.kairos.persistence.repository.user.positionCode;


import com.kairos.persistence.model.user.position.PositionCode;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;


import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_POSITION_CODE;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@Repository
public interface PositionCodeGraphRepository extends Neo4jBaseRepository<PositionCode,Long> {


    @Query("MATCH (o:Organization)-[:"+ HAS_POSITION_CODE +"]->(pn:PositionCode{deleted:false}) WHERE id(o)={0} AND pn.name=~{1} return pn ")
    PositionCode checkDuplicatePositionCode(long orgId, String positionCode);

    @Query("MATCH (o:Organization)-[:"+ HAS_POSITION_CODE +"]->(pn:PositionCode{deleted:false}) WHERE id(o)={0} AND id(pn)= {1} return pn ")
    PositionCode getPositionCodeByUnitIdAndId(long orgId, long positionCodeId);


}
