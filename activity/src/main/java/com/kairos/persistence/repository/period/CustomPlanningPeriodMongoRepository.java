package com.kairos.persistence.repository.period;

import com.kairos.activity.period.PeriodDTO;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.activity.period.PlanningPeriodDTO;
import com.mongodb.client.result.UpdateResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public interface CustomPlanningPeriodMongoRepository {

    List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId);
    List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate dateLiesInPeriod);
    UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod getFirstPlanningPeriod(Long unitId);
    PlanningPeriod getLastPlanningPeriod(Long unitId);
    boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, LocalDate startDate, LocalDate endDate, int sequence);
    boolean checkIfPeriodsExistsOrOverlapWithStartAndEndDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod findLastPlaningPeriodEndDate(Long unitId);
    List<PlanningPeriod> findAllPeriodsOfUnitByRequestPhaseId(Long unitId, String requestPhaseName);
    List<PeriodDTO> findAllPeriodsByStartDateAndLastDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod findCurrentDatePlanningPeriod(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate);
}
