package com.kairos.persistence.repository.user.client;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.client.ClientTemporaryAddress;

/**
 * Created by prabjot on 4/5/17.
 */
@Repository
public interface TemporaryAddressGraphRepository extends GraphRepository<ClientTemporaryAddress> {
}
