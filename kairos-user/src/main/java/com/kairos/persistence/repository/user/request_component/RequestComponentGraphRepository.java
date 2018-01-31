package com.kairos.persistence.repository.user.request_component;

import com.kairos.persistence.model.user.request_component.RequestComponent;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by oodles on 22/8/17.
 */
@Repository
public interface RequestComponentGraphRepository extends Neo4jBaseRepository<RequestComponent,Long>{
}
