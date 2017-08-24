package com.kairos.persistence.repository.user.client;
import com.kairos.persistence.model.user.client.ClientOrganizationRelation;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 15/11/16.
 */
@Repository
public interface ClientOrganizationRelationGraphRepository extends GraphRepository<ClientOrganizationRelation>{

    ClientOrganizationRelation findByClient();

    @Query("MATCH (c:Client),(o:Organization) where id(c)={0} AND id(o)={1} CREATE UNIQUE (c)-[r:GET_SERVICE_FROM]-(o) SET r.joinDate={2}  SET r.employmentId={3}")
    void createClientRelationWithOrganization(Long createdClientId, Long id, Long organizationId, String s);

    @Query("MATCH (c:Client)-[r:GET_SERVICE_FROM]->(o:Organization) where id(c)={0} and id(o)={1}  return count(r)")
    int checkClientOrganizationRelationship(Long clientId, Long organizatioId);

}
