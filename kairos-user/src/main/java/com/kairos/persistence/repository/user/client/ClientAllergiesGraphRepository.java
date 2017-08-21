package com.kairos.persistence.repository.user.client;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import com.kairos.persistence.model.user.client.ClientAllergies;

/**
 * Created by oodles on 9/10/16.
 */
@Repository
public interface ClientAllergiesGraphRepository extends GraphRepository<ClientAllergies>{

}
