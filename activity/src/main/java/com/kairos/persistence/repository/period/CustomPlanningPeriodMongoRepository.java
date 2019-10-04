package com.kairos.persistence.repository.period;

import com.kairos.dto.activity.period.PeriodDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by prerna on 6/4/18.
 */
public interface CustomPlanningPeriodMongoRepository {

    List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId);
    List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate dateLiesInPeriod);
    PlanningPeriod getFirstPlanningPeriod(Long unitId);
    PlanningPeriod getLastPlanningPeriod(Long unitId);
    PlanningPeriod findLastPlaningPeriodEndDate(Long unitId);
    List<PlanningPeriod> findAllPeriodsOfUnitByRequestPhaseId(Long unitId, String requestPhaseName);
    List<PeriodDTO> findAllPeriodsByStartDateAndLastDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod findCurrentDatePlanningPeriod(Long unitId, LocalDate startLocalDate, LocalDate endLocalDate);
    Phase getCurrentPhaseByDateUsingPlanningPeriod(Long unitId, LocalDate date);
    PlanningPeriodDTO findStartDateAndEndDateOfPlanningPeriodByUnitId(Long unitId);
    List<PlanningPeriod> findAllPeriodsByUnitIdAndDates(Long unitId, Set<LocalDate> localDates);
    List<PlanningPeriodDTO> findAllPlanningPeriodBetweenDatesAndUnitId(Long unitId, Date requestedStartDate, Date requestedEndDate);
    List<PlanningPeriod> findLastPlanningPeriodOfAllUnits();
    PlanningPeriod findFirstRequestPhasePlanningPeriodByUnitId(Long unitId);
}
