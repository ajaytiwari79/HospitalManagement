package com.kairos.persistence.repository.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.filter.RequiredDataForFilterDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.wrapper.ShiftResponseDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.getEndOfDay;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.CommonConstants.FULL_WEEK;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by vipul on 22/9/17.
 */
public class ShiftMongoRepositoryImpl implements CustomShiftMongoRepository {

    public static final String ACTIVITIES_ACTIVITY_ID = "activities.activityId";
    public static final String DISABLED = "disabled";
    public static final String DELETED = "deleted";
    public static final String STAFF_ID = "staffId";
    public static final String START_DATE = "startDate";
    public static final String UNIT_ID = "unitId";
    public static final String EMPLOYMENT_ID = "employmentId";
    public static final String END_DATE = "endDate";
    public static final String DRAFT_SHIFT = "draftShift";
    public static final String ACTIVITIES = "activities";
    public static final String COUNT = "count";
    public static final String CURRENT_DATE = "currentDate";
    public static final String SHIFTS_LIST = "shiftsList";
    public static final String PLANNING_PERIOD_ID = "planningPeriodId";
    public static final String ACTIVITY = "activity";
    public static final String TIME_TYPE = "timeType";
    public static final String DRAFT ="draft";
    public static final String MOSTLY_USED_COUNT = "mostlyUsedCount";
    public static final String ACTIVITY_PRIORITY = "activityPriority";
    public static final String SHIFTS ="shifts";
    private static final String ACTIVITY_STATUS = "activities.status";
    private static final String ACTIVITY_IDS = "activities.activityId";
    private static final String TIMETYPE_IDS = "activities.timeTypeId";
    private static final String PLANNED_TIME_IDS = "activities.plannedTimes.plannedTimeId";
    private static final String VALIDATED_BY_ROLES = "accessGroupRole";
    private static final String START_TIME = "shiftStartTime";
    private static final String END_TIME = "shiftEndTime";
    private static final String CTA_TEMPLATES_COLLECTION = "cTARuleTemplate";
    private static final String CTA_COLLECTION = "costTimeAgreement";
    private static final String ID = "_id";

    @Autowired
    private MongoTemplate mongoTemplate;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject private ShiftCriteriaBuilderService shiftCriteriaBuilderService;


    public List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap) {
        LocalDate currentLocalDate = DateUtils.getCurrentLocalDate();
        Criteria criteria = where(DISABLED).is(false).and(DELETED).is(false);
        List<Criteria> dynamicCriteria = new ArrayList<>();
        sickSettings.forEach(currentSickSettings -> dynamicCriteria.add(new Criteria().and(STAFF_ID).is(currentSickSettings.getStaffId())
                    .and(START_DATE).gte(currentLocalDate)
                    .lte(DateUtils.addDays(DateUtils.getDateFromLocalDate(null), activityMap.get(currentSickSettings.getActivityId()).getActivityRulesSettings().getRecurrenceDays() - 1))));

        criteria.orOperator(dynamicCriteria.toArray(new Criteria[dynamicCriteria.size()]));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public List<Shift> findAllSicknessShiftByEmploymentIdAndActivityIds(Long staffId,Collection<BigInteger> activityIds,Date startDate) {
        Criteria criteria = where(DELETED).is(false).and(START_DATE).gte(startDate).and(STAFF_ID).is(staffId);
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC,START_DATE));
        return mongoTemplate.find(query, Shift.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployments(Set<Long> employmentIds, Date startDate, Date endDate, Set<BigInteger> activityIds) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false).and("shiftType").ne(ShiftType.SICK)
                    .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false).and("shiftType").ne(ShiftType.SICK)
                    .and(START_DATE).gte(startDate).orOperator(Criteria.where(END_DATE).gte(startDate));
        }
        return getShiftWithActivityByCriteria(criteria.and(ACTIVITIES_ACTIVITY_ID).in(activityIds),false,ShiftWithActivityDTO.class);
    }

    public List<StaffShiftDetailsDTO> findAllShiftsByEmploymentsAndBetweenDuration(Set<Long> employmentIds, Date startDate, Date endDate){
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false)
                .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        return getShiftsByCriteria(criteria,false,Shift.class);
    }

    public StaffShiftDetailsDTO getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(Set<Long> employmentIds, Date startDate, Date endDate){
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false)
                .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        return getShiftForOneStaffWithByCriteria(criteria,false,Shift.class, StaffShiftDetailsDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(BigInteger shiftId,Long employmentId, Date startDate, Date endDate,Boolean draftShift) {
        return findAllShiftsBetweenDurationByEmploymentIds(shiftId,newArrayList(employmentId),startDate,endDate,draftShift);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIds(BigInteger shiftId,Collection<Long> employmentIds, Date startDate, Date endDate,Boolean draftShift) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).lt(endDate).and("_id").ne(shiftId);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).and("_id").ne(shiftId);
        }
        if(isNotNull(draftShift)){
            criteria.and(DRAFT).is(draftShift);
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIdNotEqualShiftIds(Long employmentId, Date startDate, Date endDate,List<BigInteger> shiftIds) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).lt(endDate);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate);
        }
        criteria.and("_id").nin(shiftIds);
        return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIdAndDraftShiftExists(Long employmentId, Date startDate, Date endDate,boolean draftShiftExists){
        Criteria criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).lt(endDate).and(DRAFT_SHIFT).exists(draftShiftExists);
            return draftShiftExists ? getShiftWithActivityByCriteria(criteria,true,ShiftWithActivityDTO.class): getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);

    }


    @Override
    public List<Shift> findAllShiftByIntervalAndEmploymentId(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).orOperator(Criteria.where(END_DATE).gte(startDate));
        }
        return mongoTemplate.find(new Query(criteria), Shift.class);
    }


    @Override
    public Long countByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where(DELETED).is(false).orOperator(Criteria.where(ACTIVITIES_ACTIVITY_ID).is(activityId),Criteria.where("breakActivities.activityId").is(activityId),Criteria.where("activities.childActivities.activityId").is(activityId))),
                count().as(COUNT)
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Shift.class, Map.class);
        return isCollectionNotEmpty(result.getMappedResults()) ? ((Integer) result.getMappedResults().get(0).get(COUNT)).longValue() : 0L;
    }




    public List<Long> getUnitIdListOfShiftBeforeDate(Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where(DELETED).is(false).and(DISABLED).is(false).and(END_DATE).lte(endDate)),
                project().and(UNIT_ID).as(UNIT_ID),
                group(UNIT_ID),
                sort(Sort.Direction.ASC, UNIT_ID));
        AggregationResults<HashMap> result = mongoTemplate.aggregate(aggregation, Shift.class, HashMap.class);
        return (List<Long>) result.getMappedResults().get(0).values();
    }

    public List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where(DELETED).is(false).and(DISABLED).is(false).and(UNIT_ID).is(unitId).and(END_DATE).lte(endDate))
                , project(UNIT_ID)
                        .andInclude(START_DATE)
                        .andInclude(END_DATE).andInclude(EMPLOYMENT_ID).andInclude(STAFF_ID));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }




    public List<ShiftCountDTO> getAssignedShiftsCountByEmploymentId(List<Long> employmentIds, Date startDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(EMPLOYMENT_ID).in(employmentIds).and(START_DATE).gte(startDate).and("parentOpenShiftId").exists(true)),
                group(EMPLOYMENT_ID).count().as(COUNT),
                project(COUNT).and("_id").as(EMPLOYMENT_ID),
                sort(Sort.Direction.DESC, COUNT)

        );

        AggregationResults<ShiftCountDTO> shiftCounts = mongoTemplate.aggregate(aggregation, Shift.class, ShiftCountDTO.class);

        return shiftCounts.getMappedResults();

    }

    public List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("_id").in(shiftIds).and(DISABLED).is(false)),
                project().and(DateOperators.dateOf(START_DATE).toString("%Y-%m-%d")).as(CURRENT_DATE)
                        .and("$$ROOT").as("shift"),
                group(CURRENT_DATE).push("shift").as(SHIFTS_LIST),
                project().and("_id").as(CURRENT_DATE).and(SHIFTS_LIST).as(SHIFTS)
                , sort(Sort.Direction.ASC, CURRENT_DATE)
        );
        AggregationResults<ShiftResponseDTO> shiftData = mongoTemplate.aggregate(aggregation, Shift.class, ShiftResponseDTO.class);
        return shiftData.getMappedResults();

    }

    public void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate) {

        Query query = new Query();
        query.addCriteria(where(STAFF_ID).is(staffId).and(START_DATE).gt(employmentEndDate).and(DISABLED).is(false));
        Update update = new Update();
        update.set(DELETED, true);
        mongoTemplate.updateMulti(query, update, Shift.class);

    }

    @Override
    public void updateValidateDetailsOfShift(BigInteger shiftId, AccessGroupRole accessGroupRole,LocalDate localDate) {
        Query query = new Query();
        query.addCriteria(where("_id").is(shiftId));
        Update update = new Update();
        update.set("accessGroupRole", accessGroupRole);
        update.set("validated",localDate);
        mongoTemplate.updateMulti(query, update, Shift.class);

    }


    @Override
    public Shift findShiftByShiftActivityId(BigInteger shiftActivityId) {
        Query query = new Query();
        query.addCriteria(where("activities._id").is(shiftActivityId).and(DISABLED).is(false));
        return mongoTemplate.findOne(query, Shift.class);

    }

    @Override
    public List<Shift> findShiftByShiftActivityIdAndBetweenDate(Collection<BigInteger> shiftActivityIds,LocalDate startDate,LocalDate endDate,Long staffId) {
        Criteria criteria = Criteria.where(DELETED).is(false).orOperator(where(ACTIVITIES_ACTIVITY_ID).in(shiftActivityIds),where("activities.childActivities.activityId").in(shiftActivityIds).and(DISABLED).is(false));
        if(isNotNull(startDate) && isNotNull(endDate)){
            criteria = criteria.and(START_DATE).gte(startDate).lte(endDate);
        }
        if(isNotNull(staffId)){
            criteria = criteria.and(STAFF_ID).is(staffId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Shift.class);

    }



    @Override
    public List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDate, Date endDate) {
        Query query = new Query();
        Criteria startDateCriteria = where(START_DATE).gte(startDate).lte(endDate);
        Criteria endDateCriteria = where(END_DATE).gte(startDate).lte(endDate);
        query.addCriteria(where(STAFF_ID).in(staffIds).and(DELETED).is(false)
                .and(DISABLED).is(false).orOperator(startDateCriteria, endDateCriteria));
        sort(Sort.Direction.ASC, START_DATE);
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query = new Query(where(PLANNING_PERIOD_ID).is(planningPeriodId).and("phaseId").is(phaseId).and(DISABLED).is(false));
        mongoTemplate.remove(query, Shift.class);
    }

    @Override
    public void deleteShiftBetweenDatesByEmploymentId(Long employmentId,Date startDate,Date endDate,Collection<BigInteger> shiftIds) {
        Query query = new Query(where(EMPLOYMENT_ID).is(employmentId).and(START_DATE).lt(endDate).and(END_DATE).gt(startDate).and("_id").nin(shiftIds).and(DISABLED).is(false));
        Update update = Update.update(DELETED,true);
        mongoTemplate.updateMulti(query,update, Shift.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds ) {
        return getShiftWithActivityByCriteria(Criteria.where(DELETED).is(false).and("id").in(shiftIds).and(DISABLED).is(false),false,ShiftWithActivityDTO.class);
    }

    public List<ShiftDTO> findAllByStaffIdsAndDeleteFalse(Collection<Long> staffIds, LocalDate startDate, LocalDate endDate){
        Criteria criteria = Criteria.where(DELETED).is(false).and(DISABLED).is(false).and(STAFF_ID).in(staffIds);
        if(isNotNull(startDate) && isNotNull(endDate)){
            criteria.and(START_DATE).gte(startDate).lte(getEndOfDay(asDate(endDate)));
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllDraftShiftsByIds(List<BigInteger> shiftIds,boolean draftShift ) {
        Criteria criteria = Criteria.where(DELETED).is(false).and("id").in(shiftIds).and(DRAFT_SHIFT).exists(draftShift).and(DISABLED).is(false);
        return getShiftWithActivityByCriteria(criteria,true,ShiftWithActivityDTO.class);
    }

    public List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query = new Query(where(PLANNING_PERIOD_ID).is(planningPeriodId).and("phaseId").is(phaseId).and(DISABLED).is(false));
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftBetweenDurationByUnitId(Long unitId, Date startDate, Date endDate) {
        return getShiftWithActivityByCriteria(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(DISABLED).is(false).and(START_DATE).gte(startDate).lte(endDate),false,ShiftWithActivityDTO.class);
    }

    @Override
    public List<ShiftActivityDTO> getShiftActivityByUnitIdAndActivityId(Long unitId,Date startDate,Date endDate,Set<BigInteger> activityIds){
        Set<String> activites = activityIds.stream().map(bigInteger -> bigInteger.toString()).collect(Collectors.toSet());
        Aggregation aggregation = newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(DISABLED).is(false).and(START_DATE).lt(endDate).and(END_DATE).gt(startDate)),
                project(ACTIVITIES).and("breakActivities").concatArrays(ACTIVITIES).as(ACTIVITIES),
                unwind(ACTIVITIES),
                new CustomAggregationOperation(Document.parse("{\n" +
                        "    \"$project\": {\n" +
                        "      \"activities.shiftId\": \"$_id\",\n" +
                        "        \"activities.startDate\": 1,\n" +
                        "        \"activities.endDate\": 1,\n" +
                        "        \"activities.activityId\": 1,\n" +
                        "        \"activities.breakNotHeld\": 1\n" +
                        "    }\n" +
                        "  }")),
                replaceRoot(ACTIVITIES),
                match(Criteria.where(START_DATE).lt(endDate).and(END_DATE).gt(startDate).and("activityId").in(activites).and("breakNotHeld").is(false)),
                project(START_DATE,END_DATE,"activityId","breakNotHeld","shiftId")
        );
        return mongoTemplate.aggregate(aggregation,Shift.class, ShiftActivityDTO.class).getMappedResults();
    }

    public static Document shiftWithActivityGroup() {
        String group = "{ '$group': {\n" +
                "    '_id': {\n" +
                "    '_id' : '$_id',\n" +
                "    'name' : '$name',\n" +
                "    'draft' : '$draft',\n" +
                "    'draftShift' : '$draftShift',\n" +
                "    'startDate' : '$startDate',\n" +
                "    'endDate' : '$endDate',\n" +
                "    'disabled' : '$disabled',\n" +
                "    'bid' :'$bid',\n" +
                "    'pId' :'$pId',\n" +
                "    'bonusTimeBank' : '$bonusTimeBank',\n" +
                "    'amount' : '$amount',\n" +
                "    'probability' : '$probability',\n" +
                "    'accumulatedTimeBankInMinutes' : '$accumulatedTimeBankInMinutes',\n" +
                "    'remarks' : '$remarks',\n" +
                "    'staffId' : '$staffId',\n" +
                "    'unitId' : '$unitId',\n" +
                "    'phaseId' : '$phaseId',\n" +
                "    'scheduledMinutes' : '$scheduledMinutes',\n" +
                "    'durationMinutes' :'$durationMinutes',\n" +
                "    'employmentId'  : '$employmentId' },\n" +
                "     'activities': { '$addToSet':'$activities'}\n" +
                "    }}";
        return Document.parse(group);
    }

    @Override
    public List<ShiftResponseDTO> findShiftsBetweenDurationByEmploymentIds(List<Long> employmentIds, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).in(employmentIds).and(DISABLED).is(false).and(START_DATE).lte(endDate).and(END_DATE).gte(startDate)),
                group(EMPLOYMENT_ID).push("$$ROOT").as(SHIFTS_LIST),
                project().and("_id").as(EMPLOYMENT_ID).and(SHIFTS_LIST).as(SHIFTS)
        );
        AggregationResults<ShiftResponseDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public void updateRemarkInShiftActivity(BigInteger shiftActivityId, String remark) {
        Update update = new Update().set("activities.$.remarks", remark);
        update.set("updatedAt", DateUtils.getDate());
        mongoTemplate.findAndModify(new Query(new Criteria("activities.id").is(shiftActivityId).and(DISABLED).is(false)), update, Shift.class);
    }

    @Override
    public List<Shift> findShiftsByKpiFilters(List<Long> staffIds, List<Long> unitIds, List<String> shiftActivityStatus, Set<BigInteger> timeTypeIds, Date startDate, Date endDate) {
        Criteria criteria = where(STAFF_ID).in(staffIds).and(UNIT_ID).in(unitIds).and(DELETED).is(false).and(DISABLED).is(false)
                .and(START_DATE).gte(startDate).lt(endDate);
        List<AggregationOperation> aggregationOperation = new ArrayList<>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind(ACTIVITIES));
        if (CollectionUtils.isNotEmpty(shiftActivityStatus)) {
            aggregationOperation.add(match(where("activities.status").in(shiftActivityStatus)));
        }
        if (CollectionUtils.isNotEmpty(timeTypeIds)) {
            aggregationOperation.add(lookup(ACTIVITIES, ACTIVITIES_ACTIVITY_ID, "_id", ACTIVITY));
            aggregationOperation.add(unwind(ACTIVITY));
            aggregationOperation.add(match(where("activity.activityBalanceSettings.timeTypeId").in(timeTypeIds)));
        }
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityGroup()));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<Shift> result = mongoTemplate.aggregate(aggregation, Shift.class, Shift.class);
        return result.getMappedResults();
    }





    @Override
    public List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> unitIds, List<BigInteger> activitiesIds, List<Integer> dayOfWeeks, Date startDate, Date endDate, Boolean isDraft) {
        Criteria criteria = where(STAFF_ID).in(staffIds).and(UNIT_ID).in(unitIds).and(DELETED).is(false).and(DISABLED).is(false)
                .and(START_DATE).gte(startDate).lte(endDate).and(DRAFT).is(false);
        List<AggregationOperation> aggregationOperation = new ArrayList<>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind(ACTIVITIES));
        if (CollectionUtils.isNotEmpty(activitiesIds)) {
            aggregationOperation.add(match(where(ACTIVITIES_ACTIVITY_ID).in(activitiesIds)));
        }
        aggregationOperation.add(lookup(ACTIVITIES, ACTIVITIES_ACTIVITY_ID, "_id", ACTIVITY));
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityKpiProjection()));
        if (CollectionUtils.isNotEmpty(dayOfWeeks)) {
            aggregationOperation.add(match(where("dayOfWeek").in(dayOfWeeks)));
        }
        aggregationOperation.add(new CustomAggregationOperation(Document.parse(groupByShiftAndActivity())));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        updateActivityInShift(result.getMappedResults());
        return result.getMappedResults();
    }

    public static Document shiftWithActivityKpiProjection() {
        String project = "{  \n" +
                "      '$project':{  \n" +
                "         '_id' : 1,\n" +
                "         'breakActivities' : 1,\n" +
                "    'name' : 1,\n" +
                "    'durationMinutes' : 1,\n" +
                "        'scheduledMinutes':1,\n" +
                "        'timeBankCtaBonusMinutes':1,\n" +
                "        'scheduledMinutesOfTimebank':1,\n" +
                "        'scheduledMinutesOfPayout':1,\n" +
                "        'plannedMinutesOfPayout':1,\n" +
                "        'plannedMinutesOfTimebank':1,\n" +
                "        'payoutCtaBonusMinutes':1,\n" +
                "        'createdBy':1,\n" +
                "    'staffId' : 1,\n" +
                "    'shiftType' : 1,\n" +
                "    'startDate' : 1,\n" +
                "    'endDate' : 1,\n" +
                "    'employmentId' : 1,\n" +
                "    'phaseId' : 1,\n" +
                " 'dayOfWeek': { '$dayOfWeek': '$startDate' }\n" +
                getActivitiesProjection()+"   }}";

        return Document.parse(project);
    }

    private static String getActivitiesProjection() {
        return "\t'activities.id' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities._id' : 1,\n" +
                "        'activities.breakShift' : 1,\n" +
                "        'activities.allowedBreakDurationInMinute' : 1,\n" +
                "        'activities.scheduledMinutes':1,\n" +
                "        'activities.activityName':1,\n" +
                "        'activities.status':1,\n" +
                "        'activities.timeBankCtaBonusMinutes':1,\n" +
                "        'activities.scheduledMinutesOfTimebank':1,\n" +
                "        'activities.scheduledMinutesOfPayout':1,\n" +
                "        'activities.plannedMinutesOfPayout':1,\n" +
                "        'activities.plannedMinutesOfTimebank':1,\n" +
                "        'activities.absenceReasonCodeId' : 1,\n" +
                "        'activities.reasonCodeId' : 1,\n" +
                "        'activities.remarks' : 1,\n" +
                "        'activities.payoutCtaBonusMinutes':1,\n" +
                "        'activities.plannedTimes':1,\n" +
                "        'activities.startDate' : 1,\n" +
                "        'activities.endDate' : 1,\n" +
                "        'activities.description':{ '$arrayElemAt':['$activityObject.description',0] }\n" +
                "        'activities.backgroundColor':{'$arrayElemAt':[ '$activity.activityGeneralSettings.backgroundColor',0]}\n";

    }


    private String groupByShiftAndActivity() {
        return "{'$group':{'_id':'$_id', 'durationMinutes':{'$first':'$durationMinutes'},\n" +
                "'staffId':{'$first':'$staffId'},'shiftType':{'$first':'$shiftType'},'startDate':{'$first':'$startDate'},'createdBy':{'$first':'$createdBy'},'endDate':{'$first':'$endDate'},'employmentId':{'$first':'$employmentId'},'phaseId':{'$first':'$phaseId'},'breakActivities':{'$first':'$breakActivities'},'activities':{'$addToSet':'$activities'}}}";
    }


    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentAndActivityIds(Long employmentId, Date startDate, Date endDate, Set<BigInteger> activityIds) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).lte(endDate).and(END_DATE).gte(startDate);
        } else {
            criteria = Criteria.where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DISABLED).is(false)
                    .and(START_DATE).gte(startDate).orOperator(Criteria.where(END_DATE).gte(startDate));
        }
        return getShiftWithActivityByCriteria(criteria.and(ACTIVITIES_ACTIVITY_ID).in(activityIds),false,ShiftWithActivityDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findOverlappedShiftsByEmploymentId(BigInteger shiftId, Long staffId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where(DELETED).is(false).and(STAFF_ID).is(staffId).and(START_DATE).lt(endDate).and(END_DATE).gt(startDate);
        if (isNotNull(shiftId)) {
            criteria.and("_id").ne(shiftId);
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
    }


    @Override
    public List<Shift> findAllUnPublishShiftByPlanningPeriodAndUnitId(BigInteger planningPeriodId, Long unitId, List<Long> employmentIds, List<ShiftStatus> shiftStatus) {
        Query query = new Query(where(DELETED).is(false).and(PLANNING_PERIOD_ID).is(planningPeriodId).and(UNIT_ID).is(unitId).and(DISABLED).is(false)
                .and(EMPLOYMENT_ID).in(employmentIds)
                .and(ACTIVITIES).elemMatch(where("status").nin(shiftStatus)));
        return mongoTemplate.find(query, Shift.class);

    }

    @Override
    public Long getCountOfPublishShiftByEmploymentId(Long employmentId){
        Query query = new Query(where(DELETED).is(false).and(EMPLOYMENT_ID).is(employmentId).and(DRAFT).is(false)
                .and("activities.status").is(ShiftStatus.PUBLISH));
        return mongoTemplate.count(query, Shift.class);
    }

    @Override
    public boolean absenceShiftExistsByDate(Long unitId, Date startDate, Date endDate, Long staffId) {
        Criteria criteria = where(UNIT_ID).is(unitId).and(DELETED).is(false).and(STAFF_ID).is(staffId).and(START_DATE).lt(endDate).and(END_DATE).gt(startDate).and(DISABLED).is(false);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(ACTIVITIES, ACTIVITIES_ACTIVITY_ID, "_id", ACTIVITY),
                match(new Criteria().orOperator(where("activity.activityTimeCalculationSettings.methodForCalculatingTime").is(FULL_DAY_CALCULATION), where("activity.activityTimeCalculationSettings.methodForCalculatingTime").is(FULL_WEEK))));
        return !mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class).getMappedResults().isEmpty();
    }

    private <T extends ShiftDTO> List<T> getShiftWithActivityByCriteria(Criteria criteria,boolean replaceDraftShift,Class classType,String... shiftProjection){
        List<AggregationOperation> aggregationOperations = getShiftWithActivityAggregationOperations(criteria, replaceDraftShift, shiftProjection);
        List<T> shiftWithActivityDTOS = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations),Shift.class ,classType).getMappedResults();
        updateActivityInShift(shiftWithActivityDTOS);
        return new ArrayList<>(shiftWithActivityDTOS);
    }

    //fixme this method is a  duplicate of above method
   private  List<StaffShiftDetailsDTO>  getShiftsByCriteria(Criteria criteria, boolean replaceDraftShift, Class classType){
        List<AggregationOperation> aggregationOperations = getShiftWithActivityAggregationOperations(criteria, replaceDraftShift, new String[]{});
            GroupOperation groupOperation = group(STAFF_ID).addToSet("$$ROOT").as(SHIFTS);
            aggregationOperations.add(groupOperation);
        List<StaffShiftDetailsDTO> shiftWithActivityDTOS = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations),classType , StaffShiftDetailsDTO.class).getMappedResults();
        return shiftWithActivityDTOS;
    }

    private StaffShiftDetailsDTO getShiftForOneStaffWithByCriteria(Criteria criteria, boolean replaceDraftShift, Class inputType, Class outputMappingType){
        List<AggregationOperation> aggregationOperations = getShiftWithActivityAggregationOperations(criteria, replaceDraftShift, new String[]{});
        GroupOperation groupOperation = group(STAFF_ID).addToSet("$$ROOT").as(SHIFTS);
        aggregationOperations.add(groupOperation);
        List<StaffShiftDetailsDTO> shiftWithActivityDTOS = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations),inputType ,outputMappingType).getMappedResults();
        if(isCollectionNotEmpty(shiftWithActivityDTOS)) {
            return shiftWithActivityDTOS.get(0);
        }else {
            return null;
        }

    }


   private <T extends ShiftDTO> List<T> getShiftWithActivityByCriteria(Criteria criteria,boolean replaceDraftShift,Class fromType,Class classType, boolean addActivityDetails,String... shiftProjection){
        List<AggregationOperation> aggregationOperations = getShiftWithActivityAggregationOperations(criteria, replaceDraftShift, shiftProjection);
        List<T> shiftWithActivityDTOS = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations),fromType,classType).getMappedResults();
        if(addActivityDetails) {
            updateActivityInShift(shiftWithActivityDTOS);
        }
        return shiftWithActivityDTOS;
    }

    private <T extends ShiftDTO> void updateActivityInShift(List<T> shiftWithActivityDTOS) {
        Set<BigInteger> activityIds = new HashSet<>();
        for (T shift : shiftWithActivityDTOS) {
            activityIds.addAll(getActivityIdsByShift(shift));
            if(isNotNull(shift.getDraftShift())){
                activityIds.addAll(getActivityIdsByShift(shift.getDraftShift()));
            }
        }
        Map<BigInteger, ActivityDTO> activityDTOMap = getActivityDTOMap(activityIds);
        shiftWithActivityDTOS.forEach(shift -> {
            updateActivityInShiftActivities(activityDTOMap, shift.getActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            updateActivityInShiftActivities(activityDTOMap, shift.getBreakActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            if(isNotNull(shift.getDraftShift())){
                updateActivityInShiftActivities(activityDTOMap, shift.getDraftShift().getActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
                updateActivityInShiftActivities(activityDTOMap, shift.getDraftShift().getBreakActivities(),shift.getEmploymentId(),shift.getPhaseId(),shift.getShiftType());
            }
        });
    }

    private <T extends ShiftActivityDTO> void updateActivityInShiftActivities(Map<BigInteger, ActivityDTO> activityDTOMap, List<T> shiftActivities, Long employmentId, BigInteger phaseId,ShiftType shiftType) {
        if(isCollectionNotEmpty(shiftActivities)) {
            shiftActivities.forEach(shiftActivityDTO -> {
                shiftActivityDTO.setEmploymentId(employmentId);
                shiftActivityDTO.setPhaseId(phaseId);
                shiftActivityDTO.setShiftType(shiftType);
                shiftActivityDTO.setActivity(activityDTOMap.get(shiftActivityDTO.getActivityId()));
                shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> childActivityDTO.setActivity(activityDTOMap.get(childActivityDTO.getActivityId())));
            });
        }
    }

    private <T extends ShiftDTO> Set<BigInteger> getActivityIdsByShift( T shift) {
        Set<BigInteger> activityIds = new HashSet<>();
        activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        activityIds.addAll(shift.getActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        if(isCollectionNotEmpty(shift.getBreakActivities())) {
            activityIds.addAll(shift.getBreakActivities().stream().map(ShiftActivityDTO::getActivityId).collect(Collectors.toList()));
        }
        return activityIds;
    }

    private Map<BigInteger, ActivityDTO> getActivityDTOMap(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and("_id").in(activityIds).and(DISABLED).is(false)),
                lookup("time_Type", "activityBalanceSettings.timeTypeId", "_id", TIME_TYPE)
                ,project("name","description","countryId","expertises","organizationTypes","organizationSubTypes","regions","levels","employmentTypes","tags","state", UNIT_ID,"parentId","isParentActivity","activityGeneralSettings","activityBalanceSettings","activityRulesSettings","activityIndividualPointsSettings","activityTimeCalculationSettings","activityNotesSettings","activityCommunicationSettings","activityBonusSettings","activitySkillSettings","activityOptaPlannerSetting","activityCTAAndWTASettings","activityLocationSettings","activityPhaseSettings")
                        .and(TIME_TYPE).arrayElementAt(0).as(TIME_TYPE));
        List<ActivityDTO> activityDTOS = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class).getMappedResults();
        return activityDTOS.stream().collect(Collectors.toMap(ActivityDTO::getId, v->v));
    }

    private List<AggregationOperation> getShiftWithActivityAggregationOperations(Criteria criteria, boolean replaceDraftShift, String[] shiftProjection) {
        List<AggregationOperation> aggregationOperations = newArrayList(match(criteria));
        if(replaceDraftShift){
            aggregationOperations.add(new CustomAggregationOperation(Document.parse("{\n" +
                    "  $addFields: {\n" +
                    "       \"draftShift._id\": \"$_id\",\n" +
                    "     }}")));
            aggregationOperations.add(replaceRoot(DRAFT_SHIFT));
        }
        if(shiftProjection.length>0){
            aggregationOperations.add(project(shiftProjection));
        }
        return aggregationOperations;
    }

    @Override
    public List<ActivityWithCompositeDTO> findMostlyUsedActivityByStaffId(Long staffId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(DISABLED).is(false).and(STAFF_ID).is(staffId)),
               /* new CustomAggregationOperation(Document.parse("{\n" +
                        "      $group :\n" +
                        "        {\n" +
                        "          _id : \"$activities.activityId\",\n" +
                        "          count: { $sum: 1 }\n" +
                        "        }\n" +
                        "     }")),*/
               unwind(ACTIVITIES),
               group(ACTIVITIES_ACTIVITY_ID).count().as(MOSTLY_USED_COUNT),
                lookup(ACTIVITIES, "_id", "_id", ACTIVITY)
                ,project("_id", MOSTLY_USED_COUNT).and("activity.activityPriorityId").arrayElementAt(0).as("activityPriorityId").and("activity.activityBalanceSettings.timeType").arrayElementAt(0).as("secondLevelTimtype"),
                lookup(ACTIVITY_PRIORITY, "activityPriorityId", "_id", ACTIVITY_PRIORITY),
                project("_id", MOSTLY_USED_COUNT,"secondLevelTimtype").and("activityPriority.sequence").arrayElementAt(0).as(ACTIVITY_PRIORITY)
        );
        return mongoTemplate.aggregate(aggregation, Shift.class, ActivityWithCompositeDTO.class).getMappedResults();
    }

    @Override
    public List<ShiftDTO> findAllShiftsBetweenDuration(Long employmentId, Long staffId, Date startDate, Date endDate, Long unitId, StaffFilterDTO staffFilterDTO) {
        Criteria criteria = Criteria.where(START_DATE).gte(startDate).lte(endDate);
        return getShiftByFilter(criteria, unitId, staffId, employmentId, staffFilterDTO, ShiftDTO.class);
    }


    @Override
    public List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate, StaffFilterDTO staffFilterDTO) {
        Criteria criteria = where(START_DATE).lt(endDate).and(END_DATE).gt(startDate);
        return getShiftByFilter(criteria, unitId, null, null, staffFilterDTO, ShiftDTO.class);
    }

    @Override
    public List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId, StaffFilterDTO staffFilterDTO) {
        Criteria criteria = where(START_DATE).gte(startDate).and(END_DATE).lte(endDate);
        return getShiftByFilter(criteria, unitId, staffId, null, staffFilterDTO, ShiftDTO.class);
    }

    @Override
    public List<ShiftDTO> getAllShiftBetweenDuration(Long employmentId, Long staffId, Date startDate, Date endDate, Long unitId, StaffFilterDTO staffFilterDTO) {
        Criteria criteria = Criteria.where(START_DATE).gte(startDate).lte(endDate);
        return getShiftByFilter(criteria, unitId, staffId, employmentId, staffFilterDTO, ShiftDTO.class);
    }

    @Override
    public List<Shift> findShiftByStaffIdsAndDate(List<Long> staffids, Date startDate, Date endDate, StaffFilterDTO staffFilterDTO) {
        Criteria criteria = Criteria.where(START_DATE).lt(endDate).and(END_DATE).gt(startDate);
        return getShiftByFilter(criteria, null, null, null, staffFilterDTO, Shift.class);
    }

    public <T> List<T> getShiftByFilter(Criteria criteria,Long unitId, Long staffId,Long employmentId, StaffFilterDTO staffFilterDTO,Class className){
        if(isNotNull(unitId)){
            criteria.and(UNIT_ID).is(unitId);
        }
        if(isCollectionNotEmpty(staffFilterDTO.getStaffIds())){
            criteria.and(STAFF_ID).in(staffFilterDTO.getStaffIds());
        }else if(isNotNull(staffId)){
            criteria.and(STAFF_ID).is(staffId);
        }
        if(isNotNull(employmentId)){
            criteria.and(EMPLOYMENT_ID).is(employmentId);
        }
        Map<FilterType, Set<T>> filterTypeMap = staffFilterDTO.getFiltersData().stream().filter(distinctByKey(filterSelectionDTO -> filterSelectionDTO.getName())).collect(Collectors.toMap(FilterSelectionDTO::getName, v -> v.getValue()));
        shiftCriteriaBuilderService.updateCriteria(unitId,filterTypeMap,criteria,null);
        Aggregation aggregation = Aggregation.newAggregation(match(criteria));
        return mongoTemplate.aggregate(aggregation,Shift.class,className).getMappedResults();
    }

    @Override
    public <T> List<StaffShiftDetailsDTO> getStaffListFilteredByShiftCriteria(Set<Long> staffIds, Map<FilterType, Set<T>> filterTypes, final Long unitId, Date startDate, Date endDate, boolean includeDateComparison, RequiredDataForFilterDTO requiredDataForFilterDTO) {
        Criteria criteria = Criteria.where(START_DATE).gte(startDate).and(END_DATE).lte(endDate);
        if(isNotNull(unitId)){
            criteria.and(UNIT_ID).is(unitId);
        }
        if(isCollectionNotEmpty(staffIds)){
            criteria.and(STAFF_ID).in(staffIds);
        }
        shiftCriteriaBuilderService.updateCriteria(unitId,filterTypes,criteria,requiredDataForFilterDTO);
        Aggregation aggregations = newAggregation(
                match(criteria),
                group(STAFF_ID)
        );
        return mongoTemplate.aggregate(aggregations, Shift.class, StaffShiftDetailsDTO.class).getMappedResults();
    }

}
