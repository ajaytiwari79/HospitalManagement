package com.planner.repository.locationRepository;

import com.planner.domain.location.LocationDistance;
import com.planner.repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 8/6/18
 */
@Repository
public interface LocationRepository extends MongoBaseRepository<LocationDistance,String>{
}
