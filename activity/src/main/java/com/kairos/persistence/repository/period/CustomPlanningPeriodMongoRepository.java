package com.kairos.persistence.repository.period;

import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.activity.period.PlanningPeriodDTO;
import com.mongodb.client.result.UpdateResult;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
public interface CustomPlanningPeriodMongoRepository {

//    Boolean checkUnitPeriodExistsBetweenDates(Date startDate, Date endDate, Long unitId);
    List<PlanningPeriodDTO> findAllPeriodsOfUnit(Long unitId);
//    List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, Date startDate, Date endDate);
    List<PlanningPeriodDTO> findPeriodsOfUnitByStartAndEndDate(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod getPlanningPeriodContainsDate(Long unitId, LocalDate dateLiesInPeriod);
//    PlanningPeriod getPlanningPeriodContainsDate(Long unitId, Date dateLiesInPeriod);
//    UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, Date startDate, Date endDate);
    UpdateResult deletePlanningPeriodLiesBetweenDates(Long unitId, LocalDate startDate, LocalDate endDate);
    PlanningPeriod getFirstPlanningPeriod(Long unitId);
    PlanningPeriod getLastPlanningPeriod(Long unitId);
//    boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, Date startDate, Date endDate, int sequence);
    boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, LocalDate startDate, LocalDate endDate, int sequence);
    boolean checkIfPeriodsExistsOrOverlapWithStartAndEndDate(Long unitId, LocalDate startDate, LocalDate endDate);
    List<PlanningPeriodDTO> findAllPeriodsOfUnitByCurrentPhaseId(Long unitId, BigInteger currentPhaseId);
}
