package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.user.client.ClientTeamRelation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 28/11/16.
 */
@Repository
public interface ClientTeamRelationGraphRepository extends GraphRepository<ClientTeamRelation>{


    @Query("MATCH (c:Client)-[r:SERVED_BY_TEAM]->(t:Team) where id(c)={0} AND id(t)={1} return r")
    ClientTeamRelation checkRestrictedTeam(Long clientId, long id);

    @Query("MATCH (c:Client)-[r:SERVED_BY_TEAM]->(t:Team) where id(c)={0} AND id(t)={1} return r")
    ClientTeamRelation checkPreferredTeam(Long clientId, long id);

}
