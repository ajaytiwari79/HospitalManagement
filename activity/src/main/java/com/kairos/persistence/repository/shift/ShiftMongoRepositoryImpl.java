package com.kairos.persistence.repository.shift;


import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.wrapper.ShiftResponseDTO;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.ACTIVITY;
import static com.kairos.constants.AppConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.AppConstants.FULL_WEEK;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by vipul on 22/9/17.
 */
public class ShiftMongoRepositoryImpl implements CustomShiftMongoRepository {

    private static final Logger logger = LoggerFactory.getLogger(ShiftMongoRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap) {
        LocalDate currentLocalDate = DateUtils.getCurrentLocalDate();
        Criteria criteria = where("disabled").is(false).and("deleted").is(false);
        List<Criteria> dynamicCriteria = new ArrayList<>();
        sickSettings.forEach(currentSickSettings -> {
            dynamicCriteria.add(new Criteria().and("staffId").is(currentSickSettings.getStaffId())
                    .and("startDate").gte(currentLocalDate)
                    .lte(DateUtils.addDays(DateUtils.getDateFromLocalDate(null), activityMap.get(currentSickSettings.getActivityId()).getRulesActivityTab().getRecurrenceDays() - 1)));
        });

        criteria.orOperator(dynamicCriteria.toArray(new Criteria[dynamicCriteria.size()]));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Shift.class);
    }

    public List<ShiftDTO> findAllShiftsBetweenDuration(Long employmentId, Long staffId, Date startDate, Date endDate, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("unitId").is(unitId).and("employmentId").is(employmentId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                        .and("startDate").gte(startDate).lte(endDate)),
                sort(Sort.DEFAULT_DIRECTION, "startDate")
        );
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployment(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate).lt(endDate);
        } else {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate);
        }
        Aggregation aggregation = getShiftWithActivityAggregation(criteria);
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }


    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                .and("startDate").gte(startDate).lt(endDate);
        Aggregation aggregation = getShiftWithActivityAggregation(criteria);
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }


    @Override
    public List<Shift> findAllShiftByIntervalAndEmploymentId(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").lte(endDate).and("endDate").gte(startDate);
        } else {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate).orOperator(Criteria.where("endDate").gte(startDate));
        }
        return mongoTemplate.find(new Query(criteria), Shift.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployments(List<Long> employmentIds, Date startDate, Date endDate) {
        Aggregation aggregation = getShiftWithActivityAggregation(Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).and("disabled").is(false)
                .and("startDate").lte(endDate).and("endDate").gte(startDate));
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }


    @Override
    public Long countByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                unwind("activities", true),
                match(where("deleted").is(false).and("activities.activityId").is(activityId)),
                count().as("count")
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Shift.class, Map.class);
        return (Long) result.getMappedResults().get(0).get("count");
    }


    public List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("startDate").lt(endDate).and("endDate").gt(startDate)),
                sort(Sort.Direction.ASC, "staffId"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<Long> getUnitIdListOfShiftBeforeDate(Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("deleted").is(false).and("disabled").is(false).and("endDate").lte(endDate)),
                project().and("unitId").as("unitId"),
                group("unitId"),

                sort(Sort.Direction.ASC, "unitId"));
        AggregationResults<HashMap> result = mongoTemplate.aggregate(aggregation, Shift.class, HashMap.class);
        return (List<Long>) result.getMappedResults().get(0).values();
    }

    public List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("deleted").is(false).and("disabled").is(false).and("unitId").is(unitId).and("endDate").lte(endDate))
                , project("unitId")
                        .andInclude("startDate")
                        .andInclude("endDate").andInclude("employmentId").andInclude("staffId"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }


    public List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                        .and("startDate").gte(startDate).and("endDate").lte(endDate)),
                sort(Sort.DEFAULT_DIRECTION, "startDate"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftCountDTO> getAssignedShiftsCountByEmploymentId(List<Long> employmentIds, Date startDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("employmentId").in(employmentIds).and("startDate").gte(startDate).and("parentOpenShiftId").exists(true)),
                group("employmentId").count().as("count"),
                project("count").and("_id").as("employmentId"),
                sort(Sort.Direction.DESC, "count")

        );

        AggregationResults<ShiftCountDTO> shiftCounts = mongoTemplate.aggregate(aggregation, Shift.class, ShiftCountDTO.class);

        return shiftCounts.getMappedResults();

    }

    public List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("_id").in(shiftIds)),
                project().and(DateOperators.dateOf("startDate").toString("%Y-%m-%d")).as("currentDate")
                        .and("$$ROOT").as("shift"),
                group("currentDate").push("shift").as("shiftsList"),
                project().and("_id").as("currentDate").and("shiftsList").as("shifts")
                , sort(Sort.Direction.ASC, "currentDate")
        );
        AggregationResults<ShiftResponseDTO> shiftData = mongoTemplate.aggregate(aggregation, Shift.class, ShiftResponseDTO.class);
        return shiftData.getMappedResults();

    }

    public void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate) {

        Query query = new Query();
        query.addCriteria(where("staffId").is(staffId).and("startDate").gt(employmentEndDate));
        Update update = new Update();
        update.set("deleted", true);

        mongoTemplate.updateMulti(query, update, Shift.class);

    }

    @Override
    public Shift findShiftByShiftActivityId(BigInteger shiftActivityId) {
        Query query = new Query();
        query.addCriteria(where("activities._id").is(shiftActivityId));
        return mongoTemplate.findOne(query, Shift.class);

    }

    @Override
    public List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDate, Date endDate) {
        Query query = new Query();
        Criteria startDateCriteria = where("startDate").gte(startDate).lte(endDate);
        Criteria endDateCriteria = where("endDate").gte(startDate).lte(endDate);
        query.addCriteria(where("staffId").in(staffIds).and("deleted").is(false)
                .and("disabled").is(false).orOperator(startDateCriteria, endDateCriteria));
        sort(Sort.Direction.ASC, "startDate");
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query = new Query(where("planningPeriodId").is(planningPeriodId).and("phaseId").is(phaseId));
        mongoTemplate.remove(query, Shift.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(where("deleted").is(false).and("id").in(shiftIds)),
                unwind("activities", true),
                lookup("activities", "activities.activityId", "_id", "activityObject"),
                new CustomAggregationOperation(shiftWithActivityAndDescriptionProjection()),
                new CustomAggregationOperation(shiftWithActivityGroup())
        );
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }

    public List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query = new Query(where("planningPeriodId").is(planningPeriodId).and("phaseId").is(phaseId));
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftBetweenDurationByUnitId(Long unitId, Date startDate, Date endDate) {
        Aggregation aggregation = getShiftWithActivityAggregation(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").gte(startDate).lte(endDate));
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }

    private Aggregation getShiftWithActivityAggregation(Criteria criteria) {
        return Aggregation.newAggregation(
                match(criteria),
                unwind("activities", true),
                lookup("activities", "activities.activityId", "_id", "activities.activity"),
                lookup("activities", "activityId", "_id", "activity"),
                new CustomAggregationOperation(shiftWithActivityProjection()),
                new CustomAggregationOperation(shiftWithActivityGroup()),
                new CustomAggregationOperation(anotherShiftWithActivityProjection()),
                new CustomAggregationOperation(replaceRootForShift()),
                sort(Sort.Direction.ASC, "startDate")
        );
    }

    public static Document shiftWithActivityAndDescriptionProjection() {
        String project = "{  \n" +
                "      '$project':{  \n" +
                "     '_id' : 1,\n" +
                "    'name' : 1,\n" +
                "    'startDate' : 1,\n" +
                "    'endDate' : 1,\n" +
                "    'remarks' : 1,\n" +
                "    'staffId' : 1,\n" +
                "    'unitId' : 1,\n" +
                "    'phaseId' : 1,\n" +
                "    'scheduledMinutes' : 1,\n" +
                "    'durationMinutes' : 1,\n" +
                "    'employmentId' : 1,\n" +
                "    'status':1,\n" +
                "        'activities.timeBankCtaBonusMinutes' : 1,\n" +
                "        'activities._id' : 1,\n" +
                "        'activities.status' : 1,\n" +
                "        'activities.breakShift' : 1,\n" +
                "        'activities.allowedBreakDurationInMinute' : 1,\n" +
                "        'activities.pId' : 1,\n" +
                "        'activities.id' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.startDate' : 1,\n" +
                "        'activities.endDate' : 1,\n" +
                "        'activities.scheduledMinutes' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities.plannedTimeId' : 1,\n" +
                "        'activities.absenceReasonCodeId' : 1,\n" +
                "        'activities.reasonCodeId' : 1,\n" +
                "        'activities.remarks' : 1,\n" +
                "        'activities.backgroundColor' : 1,\n" +
                "        'activities.activityName':1,\n" +
                "        'activities.plannedTimes':1,\n" +
                "        'activities.description':{ '$arrayElemAt':['$activityObject.description',0] }\n" +
                "      }\n" +
                "   }";
        return Document.parse(project);
    }

    public static Document shiftWithActivityGroup() {
        String group = "{ '$group': {\n" +
                "    '_id': {\n" +
                "    '_id' : '$_id',\n" +
                "    'name' : '$name',\n" +
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

    public static Document anotherShiftWithActivityProjection() {
        String anotherShiftWithActivityProjection = "{\n" +
                "        '$project':{\n" +
                "            '_id._id' :1,\n" +
                "    '_id.name' : 1,\n" +
                "    '_id.startDate' : 1,\n" +
                "    '_id.endDate' : 1,\n" +
                "    '_id.disabled' : 1,\n" +
                "    '_id.bid' :1,\n" +
                "    '_id.pId' :1,\n" +
                "    '_id.bonusTimeBank' : 1,\n" +
                "    '_id.amount' : 1,\n" +
                "    '_id.probability' : 1,\n" +
                "    '_id.accumulatedTimeBankInMinutes' : 1,\n" +
                "    '_id.remarks' : 1,\n" +
                "    '_id.staffId' : 1,\n" +
                "    '_id.unitId' : 1,\n" +
                "    '_id.phaseId' : 1,\n" +
                "    '_id.scheduledMinutes' : 1,\n" +
                "    '_id.durationMinutes' :1,\n" +
                "    '_id.employmentId' : 1,\n" +
                "            '_id.activities':'$activities'\n" +
                "            }\n" +
                "    }";
        return Document.parse(anotherShiftWithActivityProjection);
    }

    public static Document replaceRootForShift() {
        String replaceRootForShift = "{\n" +
                "     $replaceRoot: { newRoot: '$_id' }\n" +
                "   }";
        return Document.parse(replaceRootForShift);
    }

    @Override
    public List<ShiftResponseDTO> findShiftsBetweenDurationByEmploymentIds(List<Long> employmentIds, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).and("disabled").is(false).and("startDate").lte(endDate).and("endDate").gte(startDate)),
                group("employmentId").push("$$ROOT").as("shiftsList"),
                project().and("_id").as("employmentId").and("shiftsList").as("shifts")
        );
        AggregationResults<ShiftResponseDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftResponseDTO.class);
        return result.getMappedResults();
    }


    @Override
    public void updateRemarkInShiftActivity(BigInteger shiftActivityId, String remark) {
        Update update = new Update().set("activities.$.remarks", remark);
        update.set("updatedAt", DateUtils.getDate());
        mongoTemplate.findAndModify(new Query(new Criteria("activities.id").is(shiftActivityId)), update, Shift.class);
    }

    @Override
    public List<Shift> findShiftsByKpiFilters(List<Long> staffIds, List<Long> unitIds, List<String> shiftActivityStatus, Set<BigInteger> timeTypeIds, Date startDate, Date endDate) {
        Criteria criteria = where("staffId").in(staffIds).and("unitId").in(unitIds).and("deleted").is(false).and("disabled").is(false)
                .and("startDate").gte(startDate).lte(endDate);
        List<AggregationOperation> aggregationOperation = new ArrayList<AggregationOperation>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind("activities"));
        if (CollectionUtils.isNotEmpty(shiftActivityStatus)) {
            aggregationOperation.add(match(where("activities.status").in(shiftActivityStatus)));
        }
        if (CollectionUtils.isNotEmpty(timeTypeIds)) {
            aggregationOperation.add(lookup("activities", "activities.activityId", "_id", "activity"));
            aggregationOperation.add(unwind("activity"));
            aggregationOperation.add(match(where("activity.balanceSettingsActivityTab.timeTypeId").in(timeTypeIds)));
        }
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityGroup()));
//        aggregationOperation.add(new CustomAggregationOperation(Document.parse(projectionOfShift())));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<Shift> result = mongoTemplate.aggregate(aggregation, Shift.class, Shift.class);
        return result.getMappedResults();
    }

    @Override
    public List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> unitIds, List<BigInteger> activitiesIds, List<Integer> dayOfWeeks, Date startDate, Date endDate) {
        Criteria criteria = where("staffId").in(staffIds).and("unitId").in(unitIds).and("deleted").is(false).and("disabled").is(false)
                .and("startDate").gte(startDate).lte(endDate);
        List<AggregationOperation> aggregationOperation = new ArrayList<AggregationOperation>();
        aggregationOperation.add(new MatchOperation(criteria));
        aggregationOperation.add(unwind("activities"));
        if (CollectionUtils.isNotEmpty(activitiesIds)) {
            aggregationOperation.add(match(where("activities.activityId").in(activitiesIds)));
        }
        aggregationOperation.add(lookup("activities", "activities.activityId", "_id", "activity"));
        aggregationOperation.add(new CustomAggregationOperation(shiftWithActivityKpiProjection()));
        if (CollectionUtils.isNotEmpty(dayOfWeeks)) {
            aggregationOperation.add(match(where("dayOfWeek").in(dayOfWeeks)));
        }
        aggregationOperation.add(new CustomAggregationOperation(Document.parse(groupByShiftAndActivity())));
        Aggregation aggregation = Aggregation.newAggregation(aggregationOperation);
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }

    public static Document shiftWithActivityProjection() {
        String project = "{  \n" +
                "      '$project':{  \n" +
                "         '_id' : 1,\n" +
                "    'name' : 1,\n" +
                "    'startDate' : 1,\n" +
                "    'endDate' : 1,\n" +
                "    'disabled' : 1,\n" +
                "    'bid' :1,\n" +
                "    'pId' : 1,\n" +
                "    'bonusTimeBank' : 1,\n" +
                "    'amount' : 1,\n" +
                "    'probability' : 1,\n" +
                "    'accumulatedTimeBankInMinutes' : 1,\n" +
                "    'remarks' : 1,\n" +
                "    'staffId' : 1,\n" +
                "    'unitId' : 1,\n" +
                "    'phaseId' : 1,\n" +
                "    'scheduledMinutes' : 1,\n" +
                "    'durationMinutes' : 1,\n" +
                "    'employmentId' : 1,\n" +
                " 'dayOfWeek': { '$dayOfWeek': '$startDate' }\n" +
                "\t'status':1,\n" +
                "\t'activities.bid' : 1,\n" +
                "\t'activities.id' : 1,\n" +
                "        'activities.pId' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.startDate' : 1,\n" +
                "        'activities.endDate' : 1,\n" +
                "        'activities.scheduledMinutes' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities.plannedTimeId' : 1,\n" +
                "        'activities.remarks' : 1,\n" +
                "        'activities.status' : 1,\n" +
                "        'activities.timeType' : 1,\n" +
                "        'activities.activityName':1,\n" +
                "'activities.activity':{  \n" +
                "            '$arrayElemAt':[  \n" +
                "               '$activities.activity',\n" +
                "               0\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }";
        return Document.parse(project);
    }

    public static Document shiftWithActivityKpiProjection() {
        String project = "{  \n" +
                "      '$project':{  \n" +
                "         '_id' : 1,\n" +
                "    'name' : 1,\n" +
                "    'durationMinutes' : 1,\n" +
                "    'staffId' : 1,\n" +
                "    'startDate' : 1,\n" +
                "    'endDate' : 1,\n" +
                " 'dayOfWeek': { '$dayOfWeek': '$startDate' }\n" +
                "\t'activities.id' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities.activityName':1,\n" +
                "        'activities.backgroundColor':{  \n" +
                "            '$arrayElemAt':[  \n" +
                "               '$activity.generalActivityTab.backgroundColor',\n" +
                "               0\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }";
        return Document.parse(project);
    }

    private String groupByForPlannedHours() {
        return "{'$group':{'_id':'$staffId'}}";
    }

    private String groupByShiftAndActivity() {
        return "{'$group':{'_id':'$_id', 'durationMinutes':{'$first':'$durationMinutes'},\n" +
                "'staffId':{'$first':'$staffId'},'startDate':{'$first':'$startDate'},'endDate':{'$first':'$endDate'}'activities':{'$addToSet':'$activities'}}}";
    }

    private String projectionOfShift() {
        return "{'$project' : { 'refId' : '$_id' ,'value':'$plannedHours'} }";
    }

    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentAndActivityIds(Long employmentId, Date startDate, Date endDate, Set<BigInteger> activityIds) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").lte(endDate).and("endDate").gte(startDate);
        } else {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate).orOperator(Criteria.where("endDate").gte(startDate));
        }
        Aggregation aggregation = getShiftWithActivityAggregation(criteria.and("activities.activityId").in(activityIds));
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public boolean existShiftsBetweenDurationByEmploymentId(BigInteger shiftId, Long employmentId, Date startDate, Date endDate, ShiftType shiftType) {
        Criteria criteria = Criteria.where("disabled").is(false).and("deleted").is(false).and("employmentId").is(employmentId).and("startDate").lt(endDate).and("endDate").gt(startDate);
        if (isNotNull(shiftId)) {
            criteria.and("_id").ne(shiftId);
        }
        if (isNotNull(shiftType)) {
            criteria.and("shiftType").is(shiftType.toString());
        }
        return mongoTemplate.exists(new Query(criteria), Shift.class);
    }

    @Override
    public boolean existShiftsBetweenDurationByEmploymentIdAndTimeType(BigInteger shiftId, Long employmentId, Date startDate, Date endDate, TimeTypes timeType, boolean allowedConflicts) {
        Criteria criteria = Criteria.where("disabled").is(false).and("deleted").is(false).and("employmentId").is(employmentId).and("startDate").lt(endDate).and("endDate").gt(startDate);
        if (isNotNull(shiftId)) {
            criteria.and("_id").ne(shiftId);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("activities", "activities.activityId", "_id", "activity"),
                unwind("activity"),
                lookup("time_Type", "activity.balanceSettingsActivityTab.timeTypeId", "_id", "timeType"),
                match(where("timeType.timeTypes").is(timeType).and("timeType.allowedConflicts").is(allowedConflicts))
        );

        return !mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class).getMappedResults().isEmpty();
    }


    @Override
    public List<Shift> findAllUnPublishShiftByPlanningPeriodAndUnitId(BigInteger planningPeriodId, Long unitId, List<Long> employmentIds, List<ShiftStatus> shiftStatus) {
        Query query = new Query(where("deleted").is(false).and("planningPeriodId").is(planningPeriodId).and("unitId").is(unitId)
                .and("employmentId").in(employmentIds)
                .and("activities").elemMatch(where("status").nin(shiftStatus)));
        return mongoTemplate.find(query, Shift.class);

    }

    @Override
    public boolean absenceShiftExistsByDate(Long unitId, Date startDate, Date endDate, Long staffId) {
        Criteria criteria = where("unitId").is(unitId).and("deleted").is(false).and("staffId").is(staffId).and("startDate").lt(endDate).and("endDate").gt(startDate);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("activities", "activities.activityId", "_id", "activity"),
                match(new Criteria().orOperator(where("activity.timeCalculationActivityTab.methodForCalculatingTime").is(FULL_DAY_CALCULATION), where("activity.timeCalculationActivityTab.methodForCalculatingTime").is(FULL_WEEK))));
        return !mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class).getMappedResults().isEmpty();
    }

    private String getShiftWithPrioritiesProjection() {
        return "{  \n" +
                "      \"$project\":{  \n" +
                "         \"startDate\":1,\n" +
                "         \"endDate\":1,\n" +
                "         \"disabled\":1,\n" +
                "         \"bid\":1,\n" +
                "         \"pId\":1,\n" +
                "         \"bonusTimeBank\":1,\n" +
                "         \"amount\":1,\n" +
                "         \"probability\":1,\n" +
                "         \"accumulatedTimeBankInMinutes\":1,\n" +
                "         \"remarks\":1,\n" +
                "         \"staffId\":1,\n" +
                "         \"phaseId\":1,\n" +
                "         \"planningPeriodId\":1,\n" +
                "         \"weekCount\":1,\n" +
                "         \"unitId\":1,\n" +
                "         \"scheduledMinutes\":1,\n" +
                "         \"durationMinutes\":1,\n" +
                "         \"externalId\":1,\n" +
                "         \"employmentId\":1,\n" +
                "         \"parentOpenShiftId\":1,\n" +
                "         \"copiedFromShiftId\":1,\n" +
                "         \"sickShift\":1,\n" +
                "         \"functionId\":1,\n" +
                "         \"staffUserId\":1,\n" +
                "         \"shiftType\":1,\n" +
                "         \"timeBankCtaBonusMinutes\":1,\n" +
                "         \"dayOfWeek\":{  \n" +
                "            \"$dayOfWeek\":\"$startDate\"\n" +
                "         },\n" +
                "         \"status\":1,\n" +
                "         \"activities.activityId\":1,\n" +
                "         \"activities.startDate\":1,\n" +
                "         \"activities.endDate\":1,\n" +
                "         \"activities.scheduledMinutes\":1,\n" +
                "         \"activities.durationMinutes\":1,\n" +
                "         \"activities.activityName\":1,\n" +
                "         \"activities.bid\":1,\n" +
                "         \"activities.pId\":1,\n" +
                "         \"activities.reasonCodeId\":1,\n" +
                "         \"activities.absenceReasonCodeId\":1,\n" +
                "         \"activities.remarks\":1,\n" +
                "         \"activities._id\":1,\n" +
                "         \"activities.timeType\":1,\n" +
                "         \"activities.backgroundColor\":1,\n" +
                "         \"activities.haltBreak\":1,\n" +
                "         \"activities.plannedTimeId\":1,\n" +
                "         \"activities.breakShift\":1,\n" +
                "         \"activities.breakReplaced\":1,\n" +
                "         \"activities.timeBankCTADistributions\":1,\n" +
                "         \"activities.allowedBreakDurationInMinute\":1,\n" +
                "         \"activities.timeBankCtaBonusMinutes\":1,\n" +
                "         \"activities.startLocation\":1,\n" +
                "         \"activities.endLocation\":1,\n" +
                "         \"activities.status\":1,\n" +
                "         \"activities.activity\":{  \n" +
                "            \"$arrayElemAt\":[  \n" +
                "               \"$activities.activity\",\n" +
                "               0\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }";
    }

    private String getShiftActivityPriorityGroup() {
        return "{  \n" +
                "      \"$project\":{  \n" +
                "          \"_id\":1,\n" +
                "         \"startDate\":1,\n" +
                "         \"endDate\":1,\n" +
                "         \"disabled\":1,\n" +
                "         \"bid\":1,\n" +
                "         \"pId\":1,\n" +
                "         \"bonusTimeBank\":1,\n" +
                "         \"amount\":1,\n" +
                "         \"probability\":1,\n" +
                "         \"accumulatedTimeBankInMinutes\":1,\n" +
                "         \"remarks\":1,\n" +
                "         \"staffId\":1,\n" +
                "         \"phaseId\":1,\n" +
                "         \"planningPeriodId\":1,\n" +
                "         \"weekCount\":1,\n" +
                "         \"unitId\":1,\n" +
                "         \"scheduledMinutes\":1,\n" +
                "         \"durationMinutes\":1,\n" +
                "         \"externalId\":1,\n" +
                "         \"employmentId\":1,\n" +
                "         \"parentOpenShiftId\":1,\n" +
                "         \"copiedFromShiftId\":1,\n" +
                "         \"sickShift\":1,\n" +
                "         \"functionId\":1,\n" +
                "         \"staffUserId\":1,\n" +
                "         \"shiftType\":1,\n" +
                "         \"timeBankCtaBonusMinutes\":1,\n" +
                "         \"dayOfWeek\":{  \n" +
                "            \"$dayOfWeek\":\"$startDate\"\n" +
                "         },\n" +
                "         \"status\":1,\n" +
                "         \"activities.activityId\":1,\n" +
                "         \"activities.startDate\":1,\n" +
                "         \"activities.endDate\":1,\n" +
                "         \"activities.scheduledMinutes\":1,\n" +
                "         \"activities.durationMinutes\":1,\n" +
                "         \"activities.activityName\":1,\n" +
                "         \"activities.bid\":1,\n" +
                "         \"activities.pId\":1,\n" +
                "         \"activities.id\":1,\n" +
                "         \"activities.reasonCodeId\":1,\n" +
                "         \"activities.absenceReasonCodeId\":1,\n" +
                "         \"activities.remarks\":1,\n" +
                "         \"activities._id\":1,\n" +
                "         \"activities.timeType\":1,\n" +
                "         \"activities.backgroundColor\":1,\n" +
                "         \"activities.haltBreak\":1,\n" +
                "         \"activities.plannedTimeId\":1,\n" +
                "         \"activities.breakShift\":1,\n" +
                "         \"activities.breakReplaced\":1,\n" +
                "         \"activities.timeBankCTADistributions\":1,\n" +
                "         \"activities.allowedBreakDurationInMinute\":1,\n" +
                "         \"activities.timeBankCtaBonusMinutes\":1,\n" +
                "         \"activities.startLocation\":1,\n" +
                "         \"activities.endLocation\":1,\n" +
                "         \"activities.status\":1,\n" +
                "         \"activities.activity.activityPriority\":{  \n" +
                "            \"$arrayElemAt\":[  \n" +
                "               \"$activities.activity.activityPriority\",\n" +
                "               0\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }";
    }

    private String getSecondShiftActivityPriority() {
        return "{  \n" +
                "      \"$project\":{  \n" +
                "          \"_id\":1,\n" +
                "         \"startDate\":1,\n" +
                "         \"endDate\":1,\n" +
                "         \"disabled\":1,\n" +
                "         \"bid\":1,\n" +
                "         \"pId\":1,\n" +
                "         \"bonusTimeBank\":1,\n" +
                "         \"amount\":1,\n" +
                "         \"probability\":1,\n" +
                "         \"accumulatedTimeBankInMinutes\":1,\n" +
                "         \"remarks\":1,\n" +
                "         \"staffId\":1,\n" +
                "         \"phaseId\":1,\n" +
                "         \"planningPeriodId\":1,\n" +
                "         \"weekCount\":1,\n" +
                "         \"unitId\":1,\n" +
                "         \"scheduledMinutes\":1,\n" +
                "         \"durationMinutes\":1,\n" +
                "         \"externalId\":1,\n" +
                "         \"employmentId\":1,\n" +
                "         \"parentOpenShiftId\":1,\n" +
                "         \"copiedFromShiftId\":1,\n" +
                "         \"sickShift\":1,\n" +
                "         \"functionId\":1,\n" +
                "         \"staffUserId\":1,\n" +
                "         \"shiftType\":1,\n" +
                "         \"timeBankCtaBonusMinutes\":1,\n" +
                "         \"dayOfWeek\":{  \n" +
                "            \"$dayOfWeek\":\"$startDate\"\n" +
                "         },\n" +
                "         \"status\":1,\n" +
                "         \"activities.activityId\":1,\n" +
                "         \"activities.startDate\":1,\n" +
                "         \"activities.endDate\":1,\n" +
                "         \"activities.scheduledMinutes\":1,\n" +
                "         \"activities.durationMinutes\":1,\n" +
                "         \"activities.activityName\":1,\n" +
                "         \"activities.bid\":1,\n" +
                "         \"activities.pId\":1,\n" +
                "         \"activities.id\":1,\n" +
                "         \"activities.reasonCodeId\":1,\n" +
                "         \"activities.absenceReasonCodeId\":1,\n" +
                "         \"activities.remarks\":1,\n" +
                "         \"activities._id\":1,\n" +
                "         \"activities.timeType\":1,\n" +
                "         \"activities.backgroundColor\":1,\n" +
                "         \"activities.haltBreak\":1,\n" +
                "         \"activities.plannedTimeId\":1,\n" +
                "         \"activities.breakShift\":1,\n" +
                "         \"activities.breakReplaced\":1,\n" +
                "         \"activities.timeBankCTADistributions\":1,\n" +
                "         \"activities.allowedBreakDurationInMinute\":1,\n" +
                "         \"activities.timeBankCtaBonusMinutes\":1,\n" +
                "         \"activities.startLocation\":1,\n" +
                "         \"activities.endLocation\":1,\n" +
                "         \"activities.status\":1,\n" +
                "         \"activities.activity.activityPriority\":{  \n" +
                "            \"$arrayElemAt\":[  \n" +
                "               \"$activities.activity.activityPriority\",\n" +
                "               0\n" +
                "            ]\n" +
                "         }\n" +
                "      }\n" +
                "   }";
    }

    private String getThirdShiftActivityPriority() {
        return "{  \n" +
                "      \"$project\":{\n" +
                "        \"_id._id\":1,  \n" +
                "         \"_id.startDate\":1,\n" +
                "         \"_id.endDate\":1,\n" +
                "         \"_id.disabled\":1,\n" +
                "         \"_id.bid\":1,\n" +
                "         \"_id.pId\":1,\n" +
                "         \"_id.bonusTimeBank\":1,\n" +
                "         \"_id.amount\":1,\n" +
                "         \"_id.probability\":1,\n" +
                "         \"_id.accumulatedTimeBankInMinutes\":1,\n" +
                "         \"_id.remarks\":1,\n" +
                "         \"_id.staffId\":1,\n" +
                "         \"_id.phaseId\":1,\n" +
                "         \"_id.planningPeriodId\":1,\n" +
                "         \"_id.weekCount\":1,\n" +
                "         \"_id.unitId\":1,\n" +
                "         \"_id.scheduledMinutes\":1,\n" +
                "         \"_id.durationMinutes\":1,\n" +
                "         \"_id.externalId\":1,\n" +
                "         \"_id.employmentId\":1,\n" +
                "         \"_id.parentOpenShiftId\":1,\n" +
                "         \"_id.copiedFromShiftId\":1,\n" +
                "         \"_id.sickShift\":1,\n" +
                "         \"_id.functionId\":1,\n" +
                "         \"_id.staffUserId\":1,\n" +
                "         \"_id.shiftType\":1,\n" +
                "         \"_id.timeBankCtaBonusMinutes\":1,\n" +
                "         \"_id.dayOfWeek\":1,\n" +
                "         \"_id.status\":1,\n" +
                "         \"_id.activities\":\"$activities\"\n" +
                "      }\n" +
                "   }";
    }

}
