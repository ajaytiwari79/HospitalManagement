package com.kairos.persistence.repository.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.time_slot.TimeSlotSetService;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.LOGGER;
import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.constants.AppConstants.DELETED;
import static com.kairos.enums.FilterType.ABSENCE_ACTIVITY;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ShiftFilterRepositoryImpl implements ShiftFilterRepository {

    private static final String ACTIVITY_STATUS = "activities.status";
    private static final String ACTIVITY_IDS = "activities.activityId";
    private static final String TIMETYPE_IDS = "activities.timeTypeId";
    private static final String PLANNED_TIME_IDS = "activities.plannedTimes.plannedTimeId";
    private static final String VALIDATED_BY_ROLES = "accessGroupRole";
    private static final String UNIT_ID = "unitId";
    private static final String EMPLOYMENT_ID = "employmentId";
    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";
    private static final String START_TIME = "shiftStartTime";
    private static final String END_TIME = "shiftEndTime";
    private static final String STAFF_ID = "staffId";
    private static final String SHIFTS = "shifts";
    private static final String CTA_TEMPLATES_COLLECTION = "cTARuleTemplate";
    private static final String CTA_COLLECTION = "costTimeAgreement";
    private static final String ID = "_id";
    private static final String ACTIVITIES_COLLECTION = "activities";

    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject private TimeTypeService timeTypeService;
    @Inject
    private TimeSlotSetService timeSlotSetService;

    @Override
    public <T> List<StaffShiftDetailsDTO> getFilteredShiftsGroupedByStaff(Set<Long> employmentIds, Map<FilterType, Set<T>> filterTypes, final Long unitId, Date startDate, Date endDate, boolean includeDateComparison) {

        List<AggregationOperation> aggregationOperations = prepareOperationsListForCriteria(employmentIds, filterTypes, unitId, startDate, endDate,includeDateComparison);
        GroupOperation groupOperation = group(STAFF_ID).addToSet("$$ROOT").as(SHIFTS);
        aggregationOperations.add(groupOperation);
        Aggregation aggregations = Aggregation.newAggregation(aggregationOperations);
        return mongoTemplate.aggregate(aggregations, Shift.class, StaffShiftDetailsDTO.class).getMappedResults();
    }

    private <T> List<AggregationOperation> prepareOperationsListForCriteria(Set<Long> staffIds, Map<FilterType, Set<T>> filterTypes, final Long unitId, Date startDate, Date endDate,boolean includeDateComparison) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        List<Criteria> criteriaArrayList = new ArrayList<>();

        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId);
        criteria.and(DELETED).is(false);
        criteria.and(STAFF_ID).in(staffIds);
        if(includeDateComparison) {
            criteria.and(START_DATE).gte(startDate).and(END_DATE).lte(endDate);
        }
        Set<String> activityIds = new HashSet<>();
        LookupOperation activityTimeTypeLookupOperation = null;
        LookupOperation ctaLookupOperation = null;
        LookupOperation ctaTemplateLookupOperation = null;
        Criteria activityDetailsMatchCriteria = null;
        Criteria ctaDetailsMatchCriteria = null;

        for (Map.Entry<FilterType, Set<T>> entry : filterTypes.entrySet()) {
            switch (entry.getKey()) {
                case ACTIVITY_STATUS:
                    criteria.and(ACTIVITY_STATUS).in(entry.getValue());
                    break;
                case ACTIVITY_IDS:
                    activityIds.addAll((Set<String>) filterTypes.get(FilterType.ACTIVITY_IDS));
                    break;
                case ABSENCE_ACTIVITY:
                    activityIds.addAll((Set<String>) filterTypes.get(ABSENCE_ACTIVITY));
                    break;
                case PLANNED_TIME_TYPE:
                    criteria.and(PLANNED_TIME_IDS).in(entry.getValue());
                    break;
                case VALIDATED_BY:
                    criteria.and(VALIDATED_BY_ROLES).in(entry.getValue());
                    break;
                case TIME_SLOT:
                    prepareTimeSlotCriteria(criteria, (Set<String>) entry.getValue(), criteriaArrayList, unitId);
                    break;
                case TIME_TYPE: {
                    activityTimeTypeLookupOperation = getActivityLookupOperation(activityTimeTypeLookupOperation);
                    activityDetailsMatchCriteria = getActivityLookupTimeTypeMatchCriteria(activityDetailsMatchCriteria, (Set<String>) entry.getValue());
                    break;
                }
                case ACTIVITY_TIMECALCULATION_TYPE: {
                    activityTimeTypeLookupOperation = getActivityLookupOperation(activityTimeTypeLookupOperation);
                    activityDetailsMatchCriteria = prepareActivityTimeCalculationMatchCriteria(activityDetailsMatchCriteria, (Set<String>) entry.getValue());
                    break;
                }
                case CTA_ACCOUNT_TYPE: {
                    ctaLookupOperation = lookup(CTA_COLLECTION, EMPLOYMENT_ID, EMPLOYMENT_ID, "ctaList");
                    ctaTemplateLookupOperation = lookup(CTA_TEMPLATES_COLLECTION, "ctaList.ruleTemplateIds", ID, "ruleTemplates");
                    ctaDetailsMatchCriteria = new Criteria("ruleTemplates.plannedTimeWithFactor.accountType");
                    ctaDetailsMatchCriteria.in(entry.getValue());
                    break;
                }
                case ESCALATION_CAUSED_BY: {
                    criteria.and("shiftViolatedRules.escalationCausedBy").in(entry.getValue());
                    break;
                }
                default:

            }
        }
        if (!activityIds.isEmpty()) {
            criteria.and(ACTIVITY_IDS).in(activityIds);
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
        return aggregationOperations;
    }

    private List<Criteria> prepareMaterializedPath(final Set<String> filterValues) {
        Criteria tt;
        List<Criteria> criteriaList = new ArrayList<>();
        for (String filterValue : filterValues) {
            tt = Criteria.where("matchedActivities.path").regex(filterValue, "g");
            criteriaList.add(tt);
        }
        return criteriaList;
    }

    private LookupOperation getActivityLookupOperation(LookupOperation lookupOperation) {
        if (lookupOperation == null) {
            lookupOperation = LookupOperation.newLookup()
                    .from(ACTIVITIES_COLLECTION)
                    .localField(ACTIVITY_IDS)
                    .foreignField(ID)
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
        lookupActivityMatchCriteria.and("matchedActivities.activityTimeCalculationSettings.methodForCalculatingTime").in(values);
        return lookupActivityMatchCriteria;
    }

    private void prepareTimeSlotCriteria(final Criteria criteria, final Set<String> values, final List<Criteria> criteriaArrayList, final Long unitId) {
        List<TimeSlotDTO> timeSlotDTOS = timeSlotSetService.getUnitTimeSlotByNames(unitId, values);
        Criteria timeslotCriteria;
        for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
            timeslotCriteria = new Criteria();
            Integer startTime = (timeSlotDTO.getStartHour() * 60 * 60) + (timeSlotDTO.getStartMinute() * 60);
            timeslotCriteria.and(START_TIME).gte(startTime);
            criteriaArrayList.add(timeslotCriteria);
        }
        Criteria[] orCriteriaArray = criteriaArrayList.stream().toArray(Criteria[]::new);
        if (isNotEmpty(criteriaArrayList)) {
            criteria.orOperator(orCriteriaArray);
        }
    }

    public Set<Long> getStaffListAsIdForRealtimeCriteria(final Long unitId, Set<String> filterValues) {
        String today = DateUtils.getDateString(getDate(), "yyyy-MM-dd");
        Date dateWithoutTime = null;
        try {
            dateWithoutTime = DateUtils.convertToOnlyDate(today, "yyyy-MM-dd");
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }
        Set<Long> staffIds = new HashSet<>();
        for (String filterValue : filterValues) {
            addStaffIds(unitId, today, dateWithoutTime, staffIds, filterValue);
        }
        return staffIds;
    }

    private void addStaffIds(Long unitId, String today, Date dateWithoutTime, Set<Long> staffIds, String filterValue) {
        switch (filterValue) {
            case "ON_BREAK":
                staffIds.addAll(getStaffIdsOnBreak(unitId, dateWithoutTime));
                break;
            case "SICK":
                staffIds.addAll(getStaffSickForSelectedDay(unitId, dateWithoutTime));
                break;
            case "CURRENTLY_WORKING":
                staffIds.addAll(getStaffCurrentlyWorking(unitId, dateWithoutTime));
                break;
            case "ON_LEAVE":
                staffIds.addAll(getStaffIdsOnLeave(unitId, today));
                break;
            case "UPCOMING":
                staffIds.addAll(getStaffForUpcomingShift(unitId, dateWithoutTime));
                break;
            case "RESTING":
                staffIds.addAll(getStaffIdsOnRest(unitId, today));
                break;
            default:
        }
    }

    public Set<Long> getStaffCurrentlyWorking(Long unitId, Date date) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId);
        criteria.and("sickShift").is(false);
        criteria.and(START_DATE).lt(date).and(END_DATE).gte(date);
        query.addCriteria(criteria);
        List<StaffOnBreak> staffIdDocuments = mongoTemplate.find(query, StaffOnBreak.class);
        return staffIdDocuments.stream().map(d -> d.staffId).collect(Collectors.toSet());
    }

    public Set<Long> getStaffForUpcomingShift(Long unitId, Date date) {
        Integer currentTimeStamp = ((date.getHours() * 60 * 60) + (date.getMinutes() * 60));
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId);
        criteria.and("sickShift").is(false);
        criteria.and(START_TIME).gte(currentTimeStamp).and(START_DATE).lte(date);
        query.addCriteria(criteria);
        List<StaffOnBreak> staffIdDocuments = mongoTemplate.find(query, StaffOnBreak.class);
        return staffIdDocuments.stream().map(d -> d.staffId).collect(Collectors.toSet());
    }

    public Set<Long> getStaffIdsOnBreak(Long unitId, Date today) {
        Integer currentTimeStamp = ((today.getHours() * 60 * 60) + (today.getMinutes() * 60));
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and(UNIT_ID).is(unitId).and(START_DATE).is(today)
                .and("breakActivities.startTime").lte(currentTimeStamp)
                .and("breakActivities.endTime").gte(currentTimeStamp);
        query.addCriteria(criteria);
        List<StaffOnBreak> staffIdDocuments = mongoTemplate.find(query, StaffOnBreak.class);
        return staffIdDocuments.stream().map(d -> d.staffId).collect(Collectors.toSet());
    }


    public Set<Long> getStaffSickForSelectedDay(Long unitId, Date date) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        Criteria statusMatch = new Criteria();
        statusMatch.and(START_DATE).is(date).and(UNIT_ID).is(unitId);
        aggregationOperations.add(new MatchOperation(statusMatch));
        aggregationOperations.add(getActivityLookupOperation(null));
        aggregationOperations.add(match(Criteria.where("matchedActivities.activityRulesSettings.sicknessSettingValid").is(true)));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Map> staffIdDocuments = mongoTemplate.aggregate(aggregation, Shift.class, Map.class).getMappedResults();
        return staffIdDocuments.stream().map(d -> (Long) d.get(STAFF_ID)).collect(Collectors.toSet());
    }

    public Set<Long> getStaffIdsOnLeave(Long unitId, String date) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        Criteria statusMatch = new Criteria();
        Set<String> absenceTypes = new HashSet<>();
        Map<String, Object> dateFormatKeys = new HashMap();
        dateFormatKeys.put("format", "%Y-%m-%d");
        dateFormatKeys.put("date", "$startDate");
        Document document = new Document("$addFields", new Document("shiftStartDate", new Document("$dateToString", new Document(dateFormatKeys))));
        aggregationOperations.add(new CustomAggregationOperation(document));
        absenceTypes.add("FULL_DAY");
        absenceTypes.add("FULL_WEEK");
        statusMatch.and("shiftType").is(ShiftType.ABSENCE);
        statusMatch.and(UNIT_ID).is(unitId);
        statusMatch.and("shiftStartDate").is(date);
        aggregationOperations.add(getActivityLookupOperation(null));
        aggregationOperations.add(new MatchOperation(getActivityLookupTimeTypeMatchCriteria(statusMatch, absenceTypes)));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Map> staffIdDocuments = mongoTemplate.aggregate(aggregation, Shift.class, Map.class).getMappedResults();
        return staffIdDocuments.stream().map(d -> (Long) d.get(STAFF_ID)).collect(Collectors.toSet());
    }

    public Set<Long> getStaffIdsOnRest(Long unitId, String date) {
        final Phase phase = phaseMongoRepository.findByOrganizationIdAndPhaseEnumAndDeletedFalse(unitId, PhaseDefaultName.REALTIME);
        final List<AggregationOperation> aggregationOperations = new ArrayList<>();

        aggregationOperations.add(new CustomAggregationOperation(Document.parse(addFieldOperationForShiftTimes())));
        String matchOperation = "{\n" +
                "    \"$match\" : { \"shiftType\":\"PRESENCE\",\"unitId\":" + unitId + ",shiftEndDate:'" + date + "',\"deleted\":false  }\n" +
                "}";
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(matchOperation)));

        String lookupOperation = "{\n" +
                "    \"$lookup\" : { \"from\":\"workingTimeAgreement\",\"localField\":\"employmentId\",\"foreignField\":\"employmentId\",\"as\":\"workAgreements\"}   \n" +
                "}";
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(lookupOperation)));

        String projectOperation = "{\n" +
                "    \"$project\" : {\"workAgreements.ruleTemplateIds\":1 ,\"shiftEndTimeStamp\":1,\"currentTimeStamp\":1,\"endDate\":1,\"shiftEndDate\":1,\"workAgreements._id\":1,\"staffId\":1 } \n" +
                "}";
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(projectOperation)));

        String wtaLookup = "{\n" +
                "   \"$lookup\" : { \"from\" : \"wtaBaseRuleTemplate\",\n" +
                "             \"localField\":\"workAgreements.ruleTemplateIds\",\"foreignField\":\"_id\",\"as\":\"ruleTemplates\" } \n" +
                "}";
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(wtaLookup)));
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(matchWTAAndPhaseOperation(phase.getId().toString()))));

        Aggregation aggregation = Aggregation.newAggregation(aggregationOperations);
        List<Map> staffIdDocuments = mongoTemplate.aggregate(aggregation, SHIFTS, Map.class).getMappedResults();
        return staffIdDocuments.stream().map(d -> (Long) d.get(STAFF_ID)).collect(Collectors.toSet());
    }

    private String addFieldOperationForShiftTimes() {
        return "{\n" +
                "    \"$addFields\" : {\"shiftEndDate\" : { \"$dateToString\" : { \"format\": \"%Y-%m-%d\",\"date\":\"$endDate\" }},\n" +
                "    \"currentTimeStamp\": " + getDate().getTime() + " ,\n" +
                "     \"shiftEndTimeStamp\" : {\"$convert\" : {\"input\" : \"$endDate\",\"to\":\"long\"}} }\n" +
                "}";
    }

    private String matchWTAAndPhaseOperation(final String phaseId) {
        return "{\n" +
                "    \"$match\" : {\"ruleTemplates.wtaTemplateType\":\"DURATION_BETWEEN_SHIFTS\",\"ruleTemplates.phaseTemplateValues.phaseId\":'" + phaseId + "'" +
                "                ,\"$and\" : [{ \"$expr\":{ \"$lte\":[\"$shiftEndTimeStamp\",\"$currentTimeStamp\"]} }\n" +
                "                ,{ \"$expr\" : { \"$gte\" :[\"$shiftEndTimeStamp\", {\"$sum\" :[\"$shiftEndTimeStamp\",\"$ruleTemplates.phaseTemplateValues.staffValue\"]}] }}\n" +
                "                ]\n" +
                "              }\n" +
                "  }";

    }



    @Getter
    @Setter
    class StaffOnBreak {
        private Long staffId;
    }

}
