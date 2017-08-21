package com.kairos.persistence.repository.user.client;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.client.ClientStaffRelation;

/**
 * Created by oodles on 28/11/16.
 */
@Repository
public interface ClientStaffRelationGraphRepository extends GraphRepository<ClientStaffRelation>{
}
