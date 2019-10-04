package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.VRPClient;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Repository
public interface VRPClientGraphRepository extends Neo4jBaseRepository<VRPClient,Long> {

    @Query("MATCH (vc:VRPClient{deleted:false})-[:"+BELONGS_TO+"]-(o:Unit) where id(o)={0} return vc")
    List<VRPClient> getAllClient(Long unitId);

    @Query("MATCH (vc:VRPClient{deleted:false})-[:"+BELONGS_TO+"]-(o:Unit) where id(o)={0} return vc.installationNumber")
    List<Long> getAllClientInstalltionNo(Long unitId);

}
