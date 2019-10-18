package com.planner.repository.shift_planning;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.activity.ShiftPlanningStaffingLevelDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.shiftplanning.domain.wta.WorkingTimeAgreement;
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

    public static final String UNIT_ID = "unitId";
    public static final String END_DATE = "endDate";
    public static final String EMPLOYMENT_ID = "employmentId";
    public static final String START_DATE = "startDate";
    public static final String PHASES = "phases";
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
                match(Criteria.where(UNIT_ID).is(unitId).and("currentDate").gte(fromDate).lte(toDate)));
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
        AggregationResults<ActivityDTO> aggregationResults = mongoTemplate.aggregate(aggregation, ACTIVITIES, ActivityDTO.class);
        return aggregationResults.getMappedResults();
    }
/*******************************************CTA********************************************/
    /**
     * @param employmentIds
     * @return
     */
    public List<CTAResponseDTO> getCTARuleTemplateByEmploymentIds(List<Long> employmentIds, Date fromPlanningDate, Date toPlanningDate) {
        Criteria endDateCriteria1 = Criteria.where(END_DATE).exists(false);
        Criteria endDateCriteria2 = Criteria.where(END_DATE).gte(fromPlanningDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(START_DATE).lte(toPlanningDate).orOperator(endDateCriteria1, endDateCriteria2)),
                lookup(CTA_RULE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<CTAResponseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, COST_TIME_AGGREMENET, CTAResponseDTO.class);

        return aggregationResults.getMappedResults();
    }
/*********************************************WTA******************************************/
    /**
     * @param employmentIds
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByEmploymentIds(List<Long> employmentIds, Date fromPlanningDate, Date toPlanningDate) {
        Criteria endDateCriteria1 = Criteria.where(END_DATE).exists(false);
        Criteria endDateCriteria2 = Criteria.where(END_DATE).gte(fromPlanningDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(START_DATE).lte(toPlanningDate).orOperator(endDateCriteria1, endDateCriteria2)),
                lookup(WTABASE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<WorkingTimeAgreement> aggregationResults = mongoTemplate.aggregate(aggregation, WORKING_TIME_AGREEMENT, WorkingTimeAgreement.class);

        return aggregationResults.getMappedResults();
    }
/*********************************************Shifts******************************************/
    /**
     * @param employmentIds
     * @param fromDate
     * @param toDate
     * @return
     */
    public List<Shift> getAllShiftsByEmploymentIds(List<Long> employmentIds, Date fromDate, Date toDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(START_DATE).gte(fromDate).and(END_DATE).lte(toDate))
        );
        AggregationResults<Shift> aggregationResults = mongoTemplate.aggregate(aggregation, SHIFTS, Shift.class);
        return aggregationResults.getMappedResults();
    }


    /*******************************Queries required other then ShiftPlanningInitialization***********************************************************/
    //For country
    public List<PhaseDTO> getAllPhasesByCountryId(Long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId)));
        AggregationResults<PhaseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, PHASES, PhaseDTO.class);
        return aggregationResults.getMappedResults();
    }

    public PhaseDTO getOnePhaseById(BigInteger phaseId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(phaseId)),PhaseDTO.class, PHASES);
    }
   //For Unit(Unit)
    public List<PhaseDTO> getAllPhasesByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("organizationId").is(unitId)));
        AggregationResults<PhaseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, PHASES, PhaseDTO.class);
        return aggregationResults.getMappedResults();
    }

    public List<PhaseDTO> getPhaseByUnitIdAndPhaseEnum(List<Long> unitIds, PhaseDefaultName phaseEnum) {
        return mongoTemplate.find(new Query(Criteria.where("phaseEnum").is(phaseEnum).and("organizationId").in(unitIds)), PhaseDTO.class, PHASES);
    }

    //For Unit(Unit
    public List<PlanningPeriodDTO> getAllPlanningPeriodByUnitId(Long unitId)
    {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId)));
        AggregationResults<PlanningPeriodDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "planningPeriod", PlanningPeriodDTO.class);
        return aggregationResults.getMappedResults();
    }

    //
    public com.kairos.dto.planner.planninginfo.PlanningProblemDTO getPlanningPeriod(BigInteger planningPeriodId,Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(planningPeriodId).and(UNIT_ID).is(unitId)));
        AggregationResults<com.kairos.dto.planner.planninginfo.PlanningProblemDTO> aggregationResults = mongoTemplate.aggregate(aggregation, "planningPeriod", com.kairos.dto.planner.planninginfo.PlanningProblemDTO.class);
        return aggregationResults.getMappedResults().get(0);
    }
}
