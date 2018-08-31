package com.planner.repository.shift_planning;

import com.kairos.activity.staffing_level.ShiftPlanningStaffingLevelDTO;
import com.planner.responseDto.PlanningDto.shiftPlanningDto.ActivityDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.planner.constants.AppConstants.ACTIVITYIES;
import static com.planner.constants.AppConstants.STAFFING_LEVEL;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
/**
 * Here data comes from Activity Micro-service
 * Database{kairos}
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
     * @return
     * @author mohit
     */
    public List<ShiftPlanningStaffingLevelDTO> getShiftPlanningStaffingLevelDTOByUnitId(Long unitId, Date fromDate, Date toDate) {
        Aggregation aggregation=Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("currentDate").gte(fromDate).lte(toDate)));
        AggregationResults<ShiftPlanningStaffingLevelDTO> aggregationResults=mongoTemplate.aggregate(aggregation,STAFFING_LEVEL,   ShiftPlanningStaffingLevelDTO.class);
        return aggregationResults.getMappedResults();
    }
/***************************************************************************************/
    /**
     *
     * @param activitiesIds
     * @return
     */
    public List<ActivityDTO> getActivitiesById(Set<String> activitiesIds) {
        Aggregation aggregation=Aggregation.newAggregation(match(Criteria.where("_id").in(activitiesIds)));
        AggregationResults<ActivityDTO> aggregationResults=mongoTemplate.aggregate(aggregation,ACTIVITYIES,ActivityDTO.class);
        return aggregationResults.getMappedResults();
    }
/***************************************************************************************/
}
