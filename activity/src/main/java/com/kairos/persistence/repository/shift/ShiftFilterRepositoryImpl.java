package com.kairos.persistence.repository.shift;

import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.shift.StaffShiftDetails;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.getDate;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;

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
    private static final String STAFF_ID = "staffId";
    private static final String SHIFTS = "shifts";

    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    private UserIntegrationService userIntegrationService;

    @Override
    public <T> List<StaffShiftDetails> getFilteredShiftsGroupedByStaff(Set<Long> employmentIds, Map<FilterType, Set<T>> filterTypes, final Long unitId, Date startDate, Date endDate) {

        List<Criteria> criteriaArrayList = new ArrayList<>();
        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId);
        criteria.and(EMPLOYMENT_ID).in(employmentIds);
        criteria.and(START_DATE).gte(startDate).and(END_DATE).lte(endDate);
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        //fixme below code has to be modified with front end api, if activities are  selected, send them in one array
        Set<String> activityIds = new HashSet<>();
        LookupOperation activityTimeTypeLookupOperation = null, ctaLookupOperation = null, ctaTemplateLookupOperation = null;
        Criteria activityDetailsMatchCriteria = null, ctaDetailsMatchCriteria = null;

        for (Map.Entry<FilterType, Set<T>> entry : filterTypes.entrySet()) {
            if (entry.getKey().equals(FilterType.ACTIVITY_STATUS)) {
                criteria.and(ACTIVITY_STATUS).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.ACTIVITY_IDS)) {
                activityIds.addAll((Set<String>) filterTypes.get(FilterType.ACTIVITY_IDS));
            } else if (entry.getKey().equals(FilterType.ABSENCE_ACTIVITY)) {
                activityIds.addAll((Set<String>) filterTypes.get(FilterType.ABSENCE_ACTIVITY));
            } else if (entry.getKey().equals(FilterType.PLANNED_TIME_TYPE)) {
                criteria.and(PLANNED_TIME_IDS).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.VALIDATED_BY)) {
                criteria.and(VALIDATED_BY_ROLES).in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.TIME_SLOT)) {
                List<TimeSlotDTO> timeSlotDTOS = userIntegrationService.getUnitTimeSlotByNames(unitId, (Set<String>) entry.getValue());
                Criteria timeslotCriteria;
                for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                    timeslotCriteria = new Criteria();
                    Integer startTime = (timeSlotDTO.getStartHour() * 60 * 60) + (timeSlotDTO.getStartMinute() * 60);
                    timeslotCriteria.and(START_TIME).gte(startTime);
                    criteriaArrayList.add(timeslotCriteria);
                }
            } else if (entry.getKey().equals(FilterType.TIME_TYPE)) {
                activityTimeTypeLookupOperation = getActivityLookupOperation(activityTimeTypeLookupOperation);
                activityDetailsMatchCriteria = getActivityLookupTimeTypeMatchCriteria(activityDetailsMatchCriteria, (Set<String>) entry.getValue());
            } else if (entry.getKey().equals(FilterType.ACTIVITY_TIMECALCULATION_TYPE)) {
                activityTimeTypeLookupOperation = getActivityLookupOperation(activityTimeTypeLookupOperation);
                activityDetailsMatchCriteria = prepareActivityTimeCalculationMatchCriteria(activityDetailsMatchCriteria, (Set<String>) entry.getValue());
            } else if (entry.getKey().equals(FilterType.REAL_TIME_STATUS)) {
                prepareRealtimeStatusMatchQueries(criteriaArrayList, (Set<String>) entry.getValue());
            } else if (entry.getKey().equals(FilterType.ESCALATION_CAUSED_BY)) {
                criteria.and("shiftViolatedRules.escalationCausedBy").in(entry.getValue());
            } else if (entry.getKey().equals(FilterType.CTA_ACCOUNT_TYPE)) {
                ctaLookupOperation = lookup("costTimeAgreement", "employmentId", "employmentId", "ctaList");
                ctaTemplateLookupOperation = lookup("cTARuleTemplate", "ctaList.ruleTemplateIds", "_id", "ruleTemplates");
                ctaDetailsMatchCriteria = new Criteria("ruleTemplates.plannedTimeWithFactor.accountType");
                ctaDetailsMatchCriteria.in(entry.getValue());
            }
        }
        if (!activityIds.isEmpty()) {
            criteria.and(ACTIVITY_IDS).in(activityIds);
        }
        Criteria[] orCriteriaArray = criteriaArrayList.stream().toArray(Criteria[]::new);
        if (isNotEmpty(criteriaArrayList)) {
            criteria.orOperator(orCriteriaArray);
        }
        AggregationOperation matchOperation = new MatchOperation(criteria);
        aggregationOperations.add(matchOperation);
        if (activityTimeTypeLookupOperation != null) {
            aggregationOperations.add(activityTimeTypeLookupOperation);
            aggregationOperations.add(new MatchOperation(activityDetailsMatchCriteria));
        }
        if (ctaDetailsMatchCriteria != null) {
            aggregationOperations.add(ctaLookupOperation);
            aggregationOperations.add(ctaTemplateLookupOperation);
            aggregationOperations.add(new MatchOperation(ctaDetailsMatchCriteria));
        }

        GroupOperation groupOperation = group(STAFF_ID).addToSet("$$ROOT").as(SHIFTS);
        aggregationOperations.add(groupOperation);
        Aggregation aggregations = Aggregation.newAggregation(aggregationOperations);
        return mongoTemplate.aggregate(aggregations, Shift.class, StaffShiftDetails.class).getMappedResults();
    }

    private List<Criteria> prepareMaterializedPath(final Set<String> filterValues) {
        Criteria tt;
        List<Criteria> criteriaList = new ArrayList<>();
        for (String filterValue : filterValues) {
            tt = new Criteria();
            tt.and("matchedActivities.path").regex(filterValue, "g");
            criteriaList.add(tt);
        }
        return criteriaList;
    }

    private LookupOperation getActivityLookupOperation(LookupOperation lookupOperation) {
        if (lookupOperation == null) {
            lookupOperation = LookupOperation.newLookup()
                    .from("activities")
                    .localField(ACTIVITY_IDS)
                    .foreignField("_id")
                    .as("matchedActivities");
        }
        return lookupOperation;
    }

    private Criteria getActivityLookupTimeTypeMatchCriteria(Criteria lookupActivityMatchCriteria, Set<String> values) {
        if (lookupActivityMatchCriteria == null) {
            lookupActivityMatchCriteria = new Criteria();
        }
        List<Criteria> cc = prepareMaterializedPath(values);
        Criteria[] criteriaArray = cc.stream().toArray(Criteria[]::new);
        lookupActivityMatchCriteria.orOperator(criteriaArray);
        return lookupActivityMatchCriteria;
    }

    private Criteria prepareActivityTimeCalculationMatchCriteria(Criteria lookupActivityMatchCriteria, Set<String> values) {
        if (lookupActivityMatchCriteria == null) {
            lookupActivityMatchCriteria = new Criteria();
        }
        lookupActivityMatchCriteria.and("matchedActivities.timeCalculationActivityTab.methodForCalculatingTime").in(values);
        return lookupActivityMatchCriteria;
    }

    private void prepareRealtimeStatusMatchQueries(final List<Criteria> criteriaArrayList, Set<String> filterValues) {
        Date date = getDate();
        Criteria statusMatch = null;
        Integer currentTimeStamp = ((date.getHours() * 60 * 60) + (date.getMinutes() * 60));
        for (String filterValue : filterValues) {
            statusMatch = new Criteria();
            if (filterValue.equals("ON_BREAK")) {
                statusMatch.and("breakActivities.startTime").gte(currentTimeStamp);
            } else if (filterValue.equals("SICK")) {
                statusMatch.and("sickShift").equals(true);
                statusMatch.and(START_DATE).gte(date).lte(date);
            } else if (filterValue.equals("CURRENTLY_WORKING")) {
                statusMatch.and(START_DATE).lte(date).and(END_DATE).gte(date);
            } else if (filterValue.equals("ON_LEAVE")) {
                statusMatch.and("shiftType").equals(ShiftType.ABSENCE);
            } else if (filterValue.equals("UPCOMING")) {
                statusMatch.and(START_DATE).gt(date);
            } else if (filterValue.equals("RESTING")) {
                //todo to be implemented
            }
            criteriaArrayList.add(statusMatch);
        }


    }


}
