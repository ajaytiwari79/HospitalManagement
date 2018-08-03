package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.relationships.ClientRelativeRelation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;

/**
 * Created by oodles on 9/10/16.
 */
public interface ClientRelativeGraphRepository extends Neo4jBaseRepository<ClientRelativeRelation,Long>{

}
