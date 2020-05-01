package com.planner.service.shift_planning;

import com.planner.repository.shift_planning.ActivityMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * This service is used to interact with or have logic to collect data
 * from kairos mongodb
 *
 * @author mohit
 */
@Service
public class ActivityMongoService {

    @Inject
    private ActivityMongoRepository activityMongoRepository;

}
