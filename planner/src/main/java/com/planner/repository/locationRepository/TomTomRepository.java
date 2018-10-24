package com.planner.repository.locationRepository;

import com.planner.domain.tomtomResponse.TomTomResponse;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 8/6/18
 */
@Repository
public interface TomTomRepository extends MongoBaseRepository<TomTomResponse,String> {
}
