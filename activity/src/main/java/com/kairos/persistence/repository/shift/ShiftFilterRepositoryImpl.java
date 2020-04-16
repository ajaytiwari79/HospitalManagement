package com.kairos.persistence.repository.shift;

import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;

public class ShiftFilterRepositoryImpl implements ShiftFilterRepository {

    private static final String ACTIVITY_STATUS = "activities.status";
    private static final String ACTIVITY_IDS = "activities.activityId";
    private static final String PLANNED_TIME_IDS = "activities.plannedTimeId";
    private static final String VALIDATED_BY_ROLES = "accessGroupRole";
    private static final String UNIT_ID = "unitId";
    private static final String EMPLOYMENT_ID = "employmentId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String START_TIME = "shiftStartTime";
    private static final String END_TIME = "shiftEndTime";
    private static final String STAFF_ID = "staffId";
    private static final String SHIFTS = "shifts";

    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    private UserIntegrationService userIntegrationService;

    @Override
    public <T> List<StaffShiftDetails> getFilteredShiftsGroupedByStaff(Set<Long> employmentIds, Map<FilterType, Set<T>> filterTypes, final Long unitId, Date startDate, Date endDate) {

        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId);
        criteria.and(EMPLOYMENT_ID).in(employmentIds);
        criteria.and(START_DATE).gte(startDate).and(END_DATE).lte(endDate);

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
            } else if (entry.getKey().equals(FilterType.TIME_SLOT)) {
                List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlotByNames(unitId, (Set<String>) entry.getValue());
                Criteria timeslotCriteria;

                List<Criteria> orCriteria = new ArrayList<>();
                for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                    timeslotCriteria = new Criteria();
                    Integer startTime = (timeSlotDTO.getStartHour() * 60 * 60) + (timeSlotDTO.getStartMinute() * 60);
                    Integer endTime = (timeSlotDTO.getEndHour() * 60 * 60) + (timeSlotDTO.getEndMinute() * 60);
                    timeslotCriteria.and(START_TIME).gte(startTime);
//                            timeslotCriteria.and(END_TIME).lte(endTime);
                    orCriteria.add(timeslotCriteria);
                }
                Criteria[] criteriaArray = orCriteria.stream().toArray(Criteria[]::new);
                criteria.orOperator(criteriaArray);
            }/* else if (entry.getKey().equals(FilterType.ESCALATION_CAUSED_BY)) {
                AggregationOperation violationOperation = LookupOperation.newLookup().from("shiftViolatedRules").
                        localField("_id").foreignField("shiftId").as("violations");
                aggregationOperations.add(violationOperation);
            }*/
        }

        AggregationOperation matchOperation = new MatchOperation(criteria);
        aggregationOperations.add(matchOperation);
        GroupOperation groupOperation = group(STAFF_ID).addToSet("$$ROOT").as(SHIFTS);
        aggregationOperations.add(groupOperation);
        Aggregation aggregations = Aggregation.newAggregation(aggregationOperations);
        return mongoTemplate.aggregate(aggregations, Shift.class, StaffShiftDetails.class).getMappedResults();
    }


}
