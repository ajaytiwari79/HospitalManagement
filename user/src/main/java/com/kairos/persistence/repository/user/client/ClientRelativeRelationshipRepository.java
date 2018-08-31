package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.relationships.ClientNextToKinRelationship;
import com.kairos.persistence.model.client.relationships.ClientRelativeRelation;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRelativeRelationshipRepository extends Neo4jBaseRepository<ClientRelativeRelation, Long> {
}
