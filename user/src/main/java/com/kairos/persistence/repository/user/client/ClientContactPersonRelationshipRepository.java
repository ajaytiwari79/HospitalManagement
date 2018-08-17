package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.relationships.ClientContactPersonRelationship;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientContactPersonRelationshipRepository extends Neo4jBaseRepository<ClientContactPersonRelationship, Long> {
}
