package com.kairos.persistence.repository.shift;

import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.shift.*;
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
import org.springframework.data.mongodb.core.query.*;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftMongoRepositoryImpl.class);
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
        Criteria criteria = where("unitId").is(unitId).and("employmentId").is(employmentId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                .and("startDate").gte(startDate).lte(endDate);
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(Long employmentId, Date startDate, Date endDate) {
        Criteria criteria;
        if (Optional.ofNullable(endDate).isPresent()) {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate).lt(endDate);
        } else {
            criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate);
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIdAndDraftShiftExists(Long employmentId, Date startDate, Date endDate,boolean draftShiftExists){
        Criteria criteria = Criteria.where("deleted").is(false).and("employmentId").is(employmentId).and("disabled").is(false)
                    .and("startDate").gte(startDate).lt(endDate).and("draftShift").exists(draftShiftExists);
        if(draftShiftExists){
            return getShiftWithActivityByCriteria(criteria,true,ShiftWithActivityDTO.class);/*,new CustomAggregationOperation(Document.parse("{\n" +
                    "     $replaceRoot: { newRoot: '$draftShift' }\n" +
                    "   }")));*/
        }else {
            return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
        }
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
        return getShiftWithActivityByCriteria(Criteria.where("deleted").is(false).and("employmentId").in(employmentIds).and("disabled").is(false)
                .and("startDate").lte(endDate).and("endDate").gte(startDate),false,ShiftWithActivityDTO.class);
    }


    @Override
    public Long countByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                unwind("activities", true),
                match(where("deleted").is(false).and("activities.activityId").is(activityId)),
                count().as("count")
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Shift.class, Map.class);
        return isCollectionNotEmpty(result.getMappedResults())? ((Integer)result.getMappedResults().get(0).get("count")).longValue():0l;
    }


    public List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate) {
        Criteria criteria = where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("startDate").lt(endDate).and("endDate").gt(startDate);
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
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
        Criteria criteria = where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                .and("startDate").gte(startDate).and("endDate").lte(endDate);
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
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
    public List<Shift> findShiftByShiftActivityIdAndBetweenDate(Collection<BigInteger> shiftActivityIds,LocalDate startDate,LocalDate endDate,Long staffId) {
        Criteria criteria = where("activities.activityId").in(shiftActivityIds).and("deleted").is(false);
        if(isNotNull(startDate) && isNotNull(endDate)){
            criteria = criteria.and("startDate").gte(startDate).lte(endDate);
        }if(isNotNull(staffId)){
            criteria = criteria.and("staffId").is(staffId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Shift.class);

    }

    @Override
    public List<ShiftDTO> getAllShiftBetweenDuration(Long employmentId,Long staffId, Date startDate, Date endDate,Long unitId){
        Criteria criteria = Criteria.where("employmentId").is(employmentId).and("staffId").is(staffId).and("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("startDate").gte(startDate).lte(endDate);
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
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
    public void deleteShiftBetweenDatesByEmploymentId(Long employmentId,Date startDate,Date endDate,Collection<BigInteger> shiftIds) {
        Query query = new Query(where("employmentId").is(employmentId).and("startDate").lt(endDate).and("endDate").gt(startDate).and("_id").nin(shiftIds));
        Update update = Update.update("deleted",true);
        mongoTemplate.updateMulti(query,update, Shift.class);
    }


    @Override
    public List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds ) {
        return getShiftWithActivityByCriteria(Criteria.where("deleted").is(false).and("id").in(shiftIds),false,ShiftWithActivityDTO.class);
    }

    public List<ShiftDTO> findAllByStaffIdsAndDeleteFalse(List<Long> staffIds, LocalDate startDate, LocalDate endDate){
        Criteria criteria = Criteria.where("deleted").is(false).and("disabled").is(false).and("staffId").in(staffIds);
        if(isNotNull(startDate) && isNotNull(endDate)){
            criteria.and("startDate").gte(startDate).lte(getEndOfDay(asDate(endDate)));
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftDTO.class);
    };

    @Override
    public List<ShiftWithActivityDTO> findAllDraftShiftsByIds(List<BigInteger> shiftIds,boolean draftShift ) {
        Criteria criteria = Criteria.where("deleted").is(false).and("id").in(shiftIds).and("draftShift").exists(draftShift);
        return getShiftWithActivityByCriteria(criteria,true,ShiftWithActivityDTO.class);
    }

    public List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query = new Query(where("planningPeriodId").is(planningPeriodId).and("phaseId").is(phaseId));
        return mongoTemplate.find(query, Shift.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findAllShiftBetweenDurationByUnitId(Long unitId, Date startDate, Date endDate) {
        return getShiftWithActivityByCriteria(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").gte(startDate).lte(endDate),false,ShiftWithActivityDTO.class);
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
                "    'draftShift' : 1,\n" +
                "    'draft' : 1,\n" +
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
                "        'activities.plannedTimes':1,\n" +
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
        return getShiftWithActivityByCriteria(criteria.and("activities.activityId").in(activityIds),false,ShiftWithActivityDTO.class);
    }

    @Override
    public List<ShiftWithActivityDTO> findOverlappedShiftsByEmploymentId(BigInteger shiftId, Long employmentId, Date startDate, Date endDate) {
        Criteria criteria = Criteria.where("disabled").is(false).and("deleted").is(false).and("employmentId").is(employmentId).and("startDate").lt(endDate).and("endDate").gt(startDate);
        if (isNotNull(shiftId)) {
            criteria.and("_id").ne(shiftId);
        }
        return getShiftWithActivityByCriteria(criteria,false,ShiftWithActivityDTO.class);
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

    private <T extends ShiftDTO> List<T> getShiftWithActivityByCriteria(Criteria criteria,boolean replaceDraftShift,Class classType,String... shiftProjection){
        List<AggregationOperation> aggregationOperations = getShiftWithActivityAggregationOperations(criteria, replaceDraftShift, shiftProjection);
        List<T> shiftWithActivityDTOS = mongoTemplate.aggregate(Aggregation.newAggregation(aggregationOperations),Shift.class ,classType).getMappedResults();
        Set<BigInteger> activityIds = new HashSet<>();
        for (T shift : shiftWithActivityDTOS) {
            activityIds.addAll(shift.getActivities().stream().flatMap(shiftActivity -> shiftActivity.getChildActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
            activityIds.addAll(shift.getActivities().stream().map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toList()));
        }
        Map<BigInteger, ActivityDTO> activityDTOMap = getActivityDTOMap(activityIds);
        shiftWithActivityDTOS.forEach(shift -> {
            shift.getActivities().forEach(shiftActivityDTO -> {
                shiftActivityDTO.setActivity(activityDTOMap.get(shiftActivityDTO.getActivityId()));
                shiftActivityDTO.getChildActivities().forEach(childActivityDTO -> childActivityDTO.setActivity(activityDTOMap.get(childActivityDTO.getActivityId())));
            });
        });
        return shiftWithActivityDTOS;
    }

    private Map<BigInteger, ActivityDTO> getActivityDTOMap(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("_id").in(activityIds)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id", "timeType")
                ,project("name","description","countryId","expertises","organizationTypes","organizationSubTypes","regions","levels","employmentTypes","tags","state","unitId","parentId","isParentActivity","generalActivityTab","balanceSettingsActivityTab","rulesActivityTab","individualPointsActivityTab","timeCalculationActivityTab","notesActivityTab","communicationActivityTab","bonusActivityTab","skillActivityTab","optaPlannerSettingActivityTab","ctaAndWtaSettingsActivityTab","locationActivityTab","phaseSettingsActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType"));
        List<ActivityDTO> activityDTOS = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class).getMappedResults();
        return activityDTOS.stream().collect(Collectors.toMap(ActivityDTO::getId, v->v));
    }

    private List<AggregationOperation> getShiftWithActivityAggregationOperations(Criteria criteria, boolean replaceDraftShift, String[] shiftProjection) {
        List<AggregationOperation> aggregationOperations = newArrayList(match(criteria));
        if(replaceDraftShift){
            aggregationOperations.add(new CustomAggregationOperation(Document.parse("{\n" +
                    "  $addFields: {\n" +
                    "       \"draftShift._id\": \"$_id\",\n" +
                    "        \"draftShift.draft\": \"$draft\"\n" +
                    "     }}")));
            aggregationOperations.add(replaceRoot("draftShift"));
        }
        if(shiftProjection.length>0){
            aggregationOperations.add(project(shiftProjection));
        }
        return aggregationOperations;
    }

}
