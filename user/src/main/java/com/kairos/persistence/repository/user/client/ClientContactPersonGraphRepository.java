package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.client.ClientContactPerson;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientContactPersonGraphRepository extends Neo4jBaseRepository<ClientContactPerson, Long> {
}
