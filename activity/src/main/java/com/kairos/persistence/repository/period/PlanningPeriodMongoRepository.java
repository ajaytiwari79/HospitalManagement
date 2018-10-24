package com.kairos.persistence.repository.period;

import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public interface PlanningPeriodMongoRepository extends MongoBaseRepository<PlanningPeriod, BigInteger>, CustomPlanningPeriodMongoRepository{

    @Query(value = "{ id:?0 ,unitId:?1 }")
    PlanningPeriod findByIdAndUnitId(BigInteger id, Long unitId);

    @Query("{unitId:?0,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<PlanningPeriod> findAllByUnitIdAndBetweenDates(Long unitId,Date startDate,Date endDate);


}
