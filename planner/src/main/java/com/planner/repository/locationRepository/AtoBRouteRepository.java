package com.planner.repository.locationRepository;

import com.planner.domain.tomtomResponse.AtoBRoute;
import com.planner.domain.tomtomResponse.TomTomResponse;
import com.planner.repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 8/6/18
 */
@Repository
public interface AtoBRouteRepository extends MongoBaseRepository<AtoBRoute,String> {
}
