package com.planner.repository.shift_planning;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;

import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.shiftplanning.domain.wta.updated_wta.WorkingTimeAgreement;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.domain.shift_planning.Shift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
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

    /*******************************************StaffingLevel********************************************/
    /**
     * To get list of all StaffingLevel from
     * {@param fromDate}to{@param toDate}by
     * {@param unitId}
     *
     * @return
     * @author mohit
     */
    public List<ShiftPlanningStaffingLevelDTO> getShiftPlanningStaffingLevelDTOByUnitId(Long unitId, Date fromDate, Date toDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("currentDate").gte(fromDate).lte(toDate)));
        AggregationResults<ShiftPlanningStaffingLevelDTO> aggregationResults = mongoTemplate.aggregate(aggregation, STAFFING_LEVEL, ShiftPlanningStaffingLevelDTO.class);
        return aggregationResults.getMappedResults();
    }
/********************************************Activities*******************************************/
    /**
     * @param activitiesIds
     * @return
     */
    public List<ActivityDTO> getActivitiesById(Set<String> activitiesIds) {

        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("_id").in(activitiesIds)),
                lookup(TIME_TYPE,"balanceSettingsActivityTab.timeTypeId","_id","timeTypes"),
                project("id","name","expertises","countryId","parentId","employmentTypes")
                        .and("timeTypes").arrayElementAt(0).as("timeType"));
        AggregationResults<ActivityDTO> aggregationResults = mongoTemplate.aggregate(aggregation, ACTIVITYIES, ActivityDTO.class);
        List<ActivityDTO> activityDTOS=aggregationResults.getMappedResults();
        return activityDTOS;
    }
/*******************************************CTA********************************************/
    /**
     * @param unitPositionIds
     * @return
     */
    public List<CTAResponseDTO> getCTARuleTemplateByUnitPositionIds(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Criteria endDateCriteria1 = Criteria.where("endDate").exists(false);
        Criteria endDateCriteria2 = Criteria.where("endDate").gte(fromPlanningDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(unitPositionIds).and("startDate").lte(toPlanningDate).orOperator(endDateCriteria1, endDateCriteria2)),
                lookup(CTA_RULE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<CTAResponseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, COST_TIME_AGGREMENET, CTAResponseDTO.class);

        return aggregationResults.getMappedResults();
    }
/*********************************************WTA******************************************/
    /**
     * @param unitPositionIds
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByUnitPositionIds(List<Long> unitPositionIds, Date fromPlanningDate, Date toPlanningDate) {
        Criteria endDateCriteria1 = Criteria.where("endDate").exists(false);
        Criteria endDateCriteria2 = Criteria.where("endDate").gte(fromPlanningDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(unitPositionIds).and("startDate").lte(toPlanningDate).orOperator(endDateCriteria1, endDateCriteria2)),
                lookup(WTABASE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<WorkingTimeAgreement> aggregationResults = mongoTemplate.aggregate(aggregation, Working_Time_AGREEMENT, WorkingTimeAgreement.class);

        return aggregationResults.getMappedResults();
    }
/*********************************************Shifts******************************************/
    /**
     * @param unitPositionIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Shift> getAllShiftsByUnitPositionIds(List<Long> unitPositionIds, Date fromDate, Date toDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(unitPositionIds).and("startDate").gte(fromDate).and("endDate").lte(toDate))
        );
        AggregationResults<Shift> aggregationResults = mongoTemplate.aggregate(aggregation, SHIFTS, Shift.class);
        //return aggregationResults.getMappedResults();
        List<Shift> shifts=aggregationResults.getMappedResults();
        return shifts;
    }


    /*******************************Queries required other then ShiftPlanningInitialization***********************************************************/
    //For country
    public List<PhaseDTO> getAllPhasesByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId)));
        AggregationResults<PhaseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "phases", PhaseDTO.class);
        return aggregationResults.getMappedResults();
    }
   //For Unit(Unit)
    public List<PhaseDTO> getAllPhasesByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("organizationId").is(unitId)));
        AggregationResults<PhaseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "phases", PhaseDTO.class);
        return aggregationResults.getMappedResults();
    }

    //For Unit(Unit
    public List<PlanningPeriodDTO> getAllPlanningPeriodByUnitId(Long unitId)
    {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId)));
        AggregationResults<PlanningPeriodDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "planningPeriod", PlanningPeriodDTO.class);
        return aggregationResults.getMappedResults();
    }

    //
    public com.kairos.dto.planner.planninginfo.PlanningProblemDTO getPlanningPeriod(BigInteger planningPeriodId,Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(planningPeriodId).and("unitId").is(unitId)));
        AggregationResults<com.kairos.dto.planner.planninginfo.PlanningProblemDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "planningPeriod", com.kairos.dto.planner.planninginfo.PlanningProblemDTO.class);
        return aggregationResults.getMappedResults().get(0);
    }
}
