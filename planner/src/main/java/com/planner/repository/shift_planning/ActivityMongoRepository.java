package com.planner.repository.shift_planning;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.planner.domain.shift_planning.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.planner.constants.AppConstants.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Here data comes from Activity Micro-service
 * Database{kairos}
 *
 * @author mohit
 */
@Repository
public class ActivityMongoRepository {

    @Autowired
    @Qualifier("ActivityMongoTemplate")
    private MongoTemplate mongoTemplate;

}
