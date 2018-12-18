package com.kairos.persistence.repository.shift;


import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.wrapper.ShiftResponseDTO;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by vipul on 22/9/17.
 */
public class ShiftMongoRepositoryImpl implements CustomShiftMongoRepository {

    private static final Logger logger = LoggerFactory.getLogger(ShiftMongoRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap) {
        LocalDate currentLocalDate = DateUtils.getCurrentLocalDate();
        Criteria criteria = Criteria.where("disabled").is(false).and("deleted").is(false);
        List<Criteria> dynamicCriteria = new ArrayList<Criteria>();
        sickSettings.forEach(currentSickSettings -> {
            dynamicCriteria.add(new Criteria().and("staffId").is(currentSickSettings.getStaffId())
                    .and("startDate").gte(currentLocalDate)
                    .lte(DateUtils.addDays(DateUtils.getDateFromLocalDate(null), activityMap.get(currentSickSettings.getActivityId()).getRulesActivityTab().getRecurrenceDays() - 1)));
        });

        criteria.orOperator(dynamicCriteria.toArray(new Criteria[dynamicCriteria.size()]));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Shift.class);
    }

    public List<ShiftDTO> findAllShiftsBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("unitPositionId").is(unitPositionId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                        .and("startDate").gte(startDate).lte(endDate))
                //graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShifts")
                );
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUnitPosition(Long unitEmploymentPositionId, Date startDate, Date endDate) {
        Criteria criteria;
        if(Optional.ofNullable(endDate).isPresent()){
            criteria=Criteria.where("deleted").is(false).and("unitPositionId").is(unitEmploymentPositionId).and("disabled").is(false)
                    .and("startDate").lte(endDate).and("endDate").gte(startDate);
        }
        else{
            criteria=Criteria.where("deleted").is(false).and("unitPositionId").is(unitEmploymentPositionId).and("disabled").is(false)
                    .and("startDate").gte(startDate).orOperator(Criteria.where("endDate").gte(startDate));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                unwind("activities", true),
                lookup("activities", "activities.activityId", "_id", "activities.activity"),
                lookup("activities", "activityId", "_id", "activity"),
                new CustomAggregationOperation(shiftWithActivityProjection()),
                new CustomAggregationOperation(shiftWithActivityGroup()),
                new CustomAggregationOperation(anotherShiftWithActivityProjection()),
                new CustomAggregationOperation(replaceRootForShift())

                /*group("_id","name","startDate","endDate","disabled","bonusTimeBank","amount","probability","accumulatedTimeBankInMinutes","remarks","staffId","unitId","scheduledMinutes","durationMinutes","unitPositionId","status").addToSet("activities").as("activities"),
                project("_id._id","_id.name","_id.startDate","_id.endDate","_id.disabled","_id.pId","_id.bonusTimeBank","_id.amount","_id.probability","_id.accumulatedTimeBankInMinutes","_id.remarks","_id.staffId","_id.unitId","_id.scheduledMinutes","_id.durationMinutes","_id.unitPositionId","_id.status").and("activities").as("_id.activities")*/
                //replaceRoot("_id")

        );
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }



    @Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).and("disabled").is(false)
                        .and("startDate").lte(endDate).and("endDate").gte(startDate)),
                unwind("activities", true),
                lookup("activities", "activities.activityId", "_id", "activities.activity"),
                lookup("activities", "activityId", "_id", "activity"),
                new CustomAggregationOperation(shiftWithActivityProjection()),
                new CustomAggregationOperation(shiftWithActivityGroup()),
                new CustomAggregationOperation(anotherShiftWithActivityProjection()),
                new CustomAggregationOperation(replaceRootForShift())

                /*group("_id","name","startDate","endDate","disabled","bonusTimeBank","amount","probability","accumulatedTimeBankInMinutes","remarks","staffId","unitId","scheduledMinutes","durationMinutes","unitPositionId","status").addToSet("activities").as("activities"),
                project("_id._id","_id.name","_id.startDate","_id.endDate","_id.disabled","_id.pId","_id.bonusTimeBank","_id.amount","_id.probability","_id.accumulatedTimeBankInMinutes","_id.remarks","_id.staffId","_id.unitId","_id.scheduledMinutes","_id.durationMinutes","_id.unitPositionId","_id.status").and("activities").as("_id.activities")*/
                //replaceRoot("_id")

        );
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public Long countByActivityId(BigInteger activityId){
        Aggregation aggregation = Aggregation.newAggregation(
                unwind("activities", true),
                match(Criteria.where("deleted").is(false).and("activities.activityId").is(activityId)),
                count().as("count")
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Shift.class, Map.class);
        return (Long)result.getMappedResults().get(0).get("count");
    }


    public List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("startDate").lt(endDate).and("endDate").gt(startDate)),
                sort(Sort.Direction.ASC, "staffId"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<Long> getUnitIdListOfShiftBeforeDate(Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("disabled").is(false).and("endDate").lte(endDate)),
                project().and("unitId").as("unitId"),
                group("unitId"),

                sort(Sort.Direction.ASC, "unitId"));
        AggregationResults<HashMap> result = mongoTemplate.aggregate(aggregation, Shift.class, HashMap.class);
        return (List<Long>) result.getMappedResults().get(0).values();
    }

    public List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("disabled").is(false).and("unitId").is(unitId).and("endDate").lte(endDate))
                , project("unitId")
                        .andInclude("startDate")
                        .andInclude("endDate").andInclude("unitPositionId").andInclude("staffId"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }


    public List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId)
                        .and("startDate").gte(startDate).and("endDate").lte(endDate)),
                //graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShifts"),
                sort(Sort.Direction.ASC, "startDate"));
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftCountDTO> getAssignedShiftsCountByUnitPositionId(List<Long> unitPositionIds, Date startDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitPositionId").in(unitPositionIds).and("startDate").gte(startDate).and("parentOpenShiftId").exists(true)),
                group("unitPositionId").count().as("count"),
                project("count").and("_id").as("unitPositionId"),
                sort(Sort.Direction.DESC, "count")

        );

        AggregationResults<ShiftCountDTO> shiftCounts = mongoTemplate.aggregate(aggregation, Shift.class, ShiftCountDTO.class);

        return shiftCounts.getMappedResults();

    }

    public List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").in(shiftIds)),
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
        query.addCriteria(Criteria.where("staffId").is(staffId).and("startDate").gt(employmentEndDate));
        Update update = new Update();
        update.set("deleted", true);

        mongoTemplate.updateMulti(query, update, Shift.class);

    }
        @Override
    public Shift findShiftByShiftActivityId(BigInteger shiftActivityId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("activities._id").is(shiftActivityId));
        return mongoTemplate.findOne(query,  Shift.class);

    }
    //@Override
    public Shift findShiftToBeDone(List<Long> staffIds, Date startDate,Date endDate) {
        Query query=new Query();
        Criteria startDateCriteria=Criteria.where("startDate").gte(startDate).lte(endDate);
        Criteria endDateCriteria=Criteria.where("endDate").gte(startDate).lte(endDate);
        query.addCriteria(Criteria.where("staffId").in(staffIds).and("deleted").is(false)
                .and("disabled").is(false).orOperator(startDateCriteria,endDateCriteria));
        sort(Sort.Direction.ASC,"startDate");
        query.limit(1);
        return mongoTemplate.findOne(query,Shift.class);
    }

    @Override
    public List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDate, Date endDate) {
        Query query=new Query();
        Criteria startDateCriteria=Criteria.where("startDate").gte(startDate).lte(endDate);
        Criteria endDateCriteria=Criteria.where("endDate").gte(startDate).lte(endDate);
        query.addCriteria(Criteria.where("staffId").in(staffIds).and("deleted").is(false)
                .and("disabled").is(false).orOperator(startDateCriteria,endDateCriteria));
        sort(Sort.Direction.ASC,"startDate");
        //query.limit(1);
        return mongoTemplate.find(query,Shift.class);
    }
    @Override
    public void deleteShiftAfterRestorePhase(BigInteger planningPeriodId,BigInteger phaseId){
        Query query=new Query(Criteria.where("planningPeriodId").is(planningPeriodId).and("phaseId").is(phaseId));
        mongoTemplate.remove(query,Shift.class);
    }

    //@Override
    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByStaffUserId(Long staffUserId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffUserId").is(staffUserId).and("disabled").is(false)
                        .and("startDate").lte(endDate).and("endDate").gte(startDate)),
                unwind("activities", true),
                lookup("activities", "activities.activityId", "_id", "activities.activity"),
                lookup("activities", "activityId", "_id", "activity"),
                new CustomAggregationOperation(shiftWithActivityProjection()),
                new CustomAggregationOperation(shiftWithActivityGroup()),
                new CustomAggregationOperation(anotherShiftWithActivityProjection()),
                new CustomAggregationOperation(replaceRootForShift()));
        AggregationResults<ShiftWithActivityDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftWithActivityDTO.class);
        return result.getMappedResults();
    }
    public List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId) {
        Query query=new Query(Criteria.where("planningPeriodId").is(planningPeriodId).and("phaseId").is(phaseId));
       return mongoTemplate.find(query,Shift.class);
    }

    public static Document shiftWithActivityProjection(){
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
                "    'unitPositionId' : 1,\n" +
                "\t'status':1,\n" +
                "\t'activities.bid' : 1,\n" +
                "        'activities.pId' : 1,\n" +
                "        'activities.id' : 1,\n" +
                "        'activities.activityId' : 1,\n" +
                "        'activities.startDate' : 1,\n" +
                "        'activities.endDate' : 1,\n" +
                "        'activities.scheduledMinutes' : 1,\n" +
                "        'activities.durationMinutes' : 1,\n" +
                "        'activities.plannedTimeId' : 1,\n" +
                "        'activities.remarks' : 1,\n" +
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

    public static Document shiftWithActivityGroup(){
        String group = "{ '$group': {\n" +
                "        '_id': {\n" +
                "            '_id' : '$_id',\n" +
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
                "    'unitPositionId' : '$unitPositionId'\n" +
                "            \n" +
                "            },\n" +
                "        'activities': { \n" +
                "            '$addToSet':   '$activities'\n" +
                "            ,\n" +
                "        }\n" +
                "    }}";
        return Document.parse(group);
    }

    public static Document anotherShiftWithActivityProjection(){
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
                "    '_id.unitPositionId' : 1,\n" +
                "            '_id.activities':'$activities'\n" +
                "            }\n" +
                "    }";
        return Document.parse(anotherShiftWithActivityProjection);
    }

    public static Document replaceRootForShift(){
        String replaceRootForShift="{\n" +
                "     $replaceRoot: { newRoot: '$_id' }\n" +
                "   }";
        return Document.parse(replaceRootForShift);
    }
    @Override
    public List<ShiftResponseDTO> findShiftsBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitPositionId").in(unitPositionIds).and("disabled").is(false).and("startDate").lte(endDate).and("endDate").gte(startDate)),
                group("unitPositionId").push("$$ROOT").as("shiftsList"),
                project().and("_id").as("unitPositionId").and("shiftsList").as("shifts")
        );
        AggregationResults<ShiftResponseDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftResponseDTO.class);
        return result.getMappedResults();
    }



}
