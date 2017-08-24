package com.kairos.persistence.repository.user.client;

import com.kairos.persistence.model.user.client.ClientRelativeRelation;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by oodles on 9/10/16.
 */
public interface ClientRelativeGraphRepository extends GraphRepository<ClientRelativeRelation>{

}
