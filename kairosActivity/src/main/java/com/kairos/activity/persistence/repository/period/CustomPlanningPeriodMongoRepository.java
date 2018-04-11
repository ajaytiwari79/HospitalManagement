package com.kairos.activity.persistence.repository.period;

import com.kairos.activity.persistence.model.period.PlanningPeriod;
import com.kairos.response.dto.web.period.PlanningPeriodDTO;
import com.mongodb.client.result.UpdateResult;

import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public interface CustomPlanningPeriodMongoRepository {

    Boolean checkUnitPeriodExistsBetweenDates(Date startDate, Date endDate, Long unitId);
    List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId);
    List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, Date startDate, Date endDate);
    PlanningPeriod getPlanningPeriodContainsDate(Long unitId, Date dateLiesInPeriod);
    UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, Date startDate, Date endDate);
}
