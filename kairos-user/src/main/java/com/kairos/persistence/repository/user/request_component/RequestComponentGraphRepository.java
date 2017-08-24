package com.kairos.persistence.repository.user.request_component;

import com.kairos.persistence.model.user.request_component.RequestComponent;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 22/8/17.
 */
@Repository
public interface RequestComponentGraphRepository extends GraphRepository<RequestComponent>{
}
