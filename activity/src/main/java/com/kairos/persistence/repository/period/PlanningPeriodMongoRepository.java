package com.kairos.persistence.repository.period;

import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public interface PlanningPeriodMongoRepository extends MongoBaseRepository<PlanningPeriod, BigInteger>, CustomPlanningPeriodMongoRepository {

    @Query(value = "{deleted:false,id:?0 ,unitId:?1 }")
    PlanningPeriod findByIdAndUnitId(BigInteger id, Long unitId);

    @Query("{deleted:false,unitId:?0,startDate: {$lte: ?2},endDate:{$gte:?1}}")
    List<PlanningPeriod> findAllByUnitIdAndBetweenDates(Long unitId, Date startDate, Date endDate);

    @Query("{deleted:false,unitId:?0,id:{$in:?1}}")
    List<PlanningPeriod> findAllByUnitIdAndIds(Long unitId, List<BigInteger> planningPeriodIds);

    @Query("{deleted:false,unitId:{$in:?0},startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<PlanningPeriod> findAllByUnitIdsAndBetweenDates(List<Long> unitIds, Date startDate, Date endDate);

    @Query("{deleted:false,unitId:?0,startDate: {$lte: ?1},endDate:{$gte:?1}}")
    PlanningPeriod findOneByUnitIdAndDate(Long unitId, Date startDate);

}
