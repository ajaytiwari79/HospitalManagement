package com.planner.repository.locationRepository;

import com.planner.domain.location.PlanningLocation;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author pradeep
 * @date - 8/6/18
 */
@Repository
public interface PlanningLocationRepository extends MongoBaseRepository<PlanningLocation,String> {

    @Query("{latitude:?0,longitude:?1}")
    PlanningLocation getLocationByLatLong(double latitude, double logitude);
}
