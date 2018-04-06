package com.kairos.activity.persistence.repository.period;

import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;

/**
 * Created by prerna on 6/4/18.
 */
public interface PlanningPeriodMongoRepository extends MongoBaseRepository<PlanningPeriod, BigInteger>, CustomPlanningPeriodMongoRepository{
}
