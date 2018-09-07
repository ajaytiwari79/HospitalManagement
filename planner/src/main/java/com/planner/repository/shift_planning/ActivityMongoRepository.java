package com.planner.repository.shift_planning;

import com.kairos.activity.cta.CTAResponseDTO;
import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.kairos.shiftplanning.domain.cta.CTARuleTemplate;
import com.planner.domain.wta.templates.WorkingTimeAgreement;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.planner.constants.AppConstants.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * Here data comes from Activity Micro-service
 * Database{kairos}
 *
 * @author mohit
 */
@Repository
public class ActivityMongoRepository {

    @Inject
    @Qualifier("ActivityMongoTemplate")
    private MongoTemplate mongoTemplate;

    /***************************************************************************************/
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
/***************************************************************************************/
    /**
     * @param activitiesIds
     * @return
     */
    public List<ActivityDTO> getActivitiesById(Set<String> activitiesIds) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("_id").in(activitiesIds)));
        AggregationResults<ActivityDTO> aggregationResults = mongoTemplate.aggregate(aggregation, ACTIVITYIES, ActivityDTO.class);
        return aggregationResults.getMappedResults();
    }
/***************************************************************************************/
    /**
     * @param unitPositionIds
     * @return
     */
    public List<CTAResponseDTO> getCTARuleTemplateByUnitPositionIds(Long[] unitPositionIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(Arrays.asList(unitPositionIds))),
                lookup(CTA_RULE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<CTAResponseDTO> aggregationResults = mongoTemplate.aggregate(aggregation, COST_TIME_AGGREMENET, CTAResponseDTO.class);

        return aggregationResults.getMappedResults();
    }

    /**
     *
     * @param unitPositionIds
     * @return
     */
    public List<WorkingTimeAgreement> getWTARuleTemplateByUnitPositionIds(Long[] unitPositionIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(Arrays.asList(unitPositionIds))),
                lookup(WTABASE_TEMPLATE, "ruleTemplateIds", "_id", "ruleTemplates"));
        AggregationResults<WorkingTimeAgreement> aggregationResults = mongoTemplate.aggregate(aggregation, Working_Time_AGREEMENT, WorkingTimeAgreement.class);

        return aggregationResults.getMappedResults();
    }
}
