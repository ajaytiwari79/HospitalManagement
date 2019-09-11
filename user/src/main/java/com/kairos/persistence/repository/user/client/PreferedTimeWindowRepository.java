package com.kairos.persistence.repository.user.client;


import com.kairos.persistence.model.client.PreferedTimeWindow;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * @author pradeep
 * @date - 28/6/18
 */
@Repository
public interface PreferedTimeWindowRepository extends Neo4jBaseRepository<PreferedTimeWindow,Long> {

    @Query("MATCH (pre:PreferedTimeWindow{deleted:false})-[:"+BELONGS_TO+"]-(o:Unit) where id(o)={0} return pre")
    List<PreferedTimeWindow> getAllByUnitId(Long unitId);
}
