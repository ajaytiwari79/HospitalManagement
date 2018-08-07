package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.relationships.ClientOrganizationRelation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.stereotype.Repository;

import static com.kairos.persistence.model.constants.RelationshipConstants.GET_SERVICE_FROM;

/**
 * Created by oodles on 15/11/16.
 */
@Repository
public interface ClientOrganizationRelationGraphRepository extends Neo4jBaseRepository<ClientOrganizationRelation,Long>{

    ClientOrganizationRelation findByClient();

    @Query("MATCH (c:Client),(o:Organization) where id(c)={0} AND id(o)={1} CREATE (c)-[r:"+GET_SERVICE_FROM+"]->(o) SET r.joinDate={2}  SET r.employmentId={3}")
    void createClientRelationWithOrganization(Long createdClientId, Long unitId, Long joiningDate, String employmentId);

    @Query("MATCH (c:Client)-[r:"+GET_SERVICE_FROM+"]->(o:Organization) where id(c)={0} and id(o)={1}  return count(r)")
    int checkClientOrganizationRelationship(Long clientId, Long organizationId);

}
