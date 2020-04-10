package com.kairos.persistence.repository.shift;

import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftFilterRepositoryImpl implements ShiftFilterRepository {

    public static final String ACTIVITY_STATUS = "activities.status";
    public static final String ACTIVITY_IDS = "activities.activityId";
    public static final String PLANNED_TIME_IDS = "activities.plannedTimeId";
    public static final String VALIDATED_BY_ROLES = "accessGroupRole";
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public <T> List<StaffShiftDetails> getFilteredShiftsGroupedByStaff(Map<FilterType, Set<T>> filterTypes) {
        Criteria criteria = new Criteria();
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        for (Map.Entry<FilterType, Set<T>> entry : filterTypes.entrySet()) {
            if (entry.getKey().equals(FilterType.ACTIVITY_STATUS)) {
                criteria.and(ACTIVITY_STATUS).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.ACTIVITY_IDS)) {
                criteria.and(ACTIVITY_IDS).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.PLANNED_TIME_TYPE)) {
                criteria.and(PLANNED_TIME_IDS).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.VALIDATED_BY)) {
                criteria.and(VALIDATED_BY_ROLES).in(entry.getValue());
            }
           /* else if (entry.getKey().equals(FilterType.TIME_SLOT)) {

            } else if (entry.getKey().equals(FilterType.ESCALATION_CAUSED_BY)) {
                AggregationOperation violationOperation = LookupOperation.newLookup().from("shiftViolatedRules").
                        localField("_id").foreignField("shiftId").as("violations");
                aggregationOperations.add(violationOperation);
            }*/
        }

        AggregationOperation matchOperation = new MatchOperation(criteria);
        aggregationOperations.add(matchOperation);
        Aggregation aggregations = Aggregation.newAggregation(aggregationOperations);

        return mongoTemplate.aggregate(aggregations, Shift.class, StaffShiftDetails.class).getMappedResults();
    }
}
