package com.kairos.persistence.repository.shift;


import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftQueryResult;
import com.kairos.dto.activity.shift.ShiftTimeDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.service.counter.ShiftFilterCriteria;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
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
                match(Criteria.where("unitId").is(unitId).and("unitPositionId").is(unitPositionId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId).and("isMainShift").is(true)
                        .and("startDate").gte(startDate).and("endDate").lte(endDate))
                //graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShifts")
                );
        AggregationResults<ShiftDTO> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftDTO.class);
        return result.getMappedResults();
    }

    public List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isMainShift").is(true).and("unitPositionId").is(unitEmploymentPositionId).and("disabled").is(false)
                        .and("startDate").lte(endDate).and("endDate").gte(startDate)),
                //graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShift"),
                unwind("subShifts", true),
                lookup("activities", "subShift.activityId", "_id", "subShift.activity"),
                lookup("activities", "activityId", "_id", "activity")
                , project("unitId")
                        .andInclude("deleted")
                        .andInclude("startDate")
                        .andInclude("endDate").andInclude("scheduledMinutes").andInclude("durationMinutes")
                        .andInclude("isMainShift").andInclude("subShift")
                        //.andInclude("subShift.startDate").andInclude("subShift.endDate")
                        .andInclude("subShift.activity")
                        .and("activity").arrayElementAt(0).as("activity")
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
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("startDate").gte(startDate).and("endDate").lt(endDate)),
                project("unitId", "startDate", "endDate", "activityId", "staffId", "unitPositionId", "status", "allowedBreakDurationInMinute"),
                sort(Sort.Direction.ASC, "staffId"));


        //AggregationResults<Object> result1 = mongoTemplate.aggregate(aggregation, Shift.class, Object.class);
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
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("disabled").is(false).and("staffId").is(staffId).and("isMainShift").is(true)
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

    public List<DateWiseShiftResponse> findAllByIdGroupByDate(List<BigInteger> shiftIds) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").in(shiftIds)),
                project().and(DateOperators.dateOf("startDate").toString("%Y-%m-%d")).as("currentDate")
                        .and("$$ROOT").as("shift"),
                group("currentDate").push("shift").as("shiftsList"),
                project().and("_id").as("currentDate").and("shiftsList").as("shifts")
                , sort(Sort.Direction.ASC, "currentDate")
        );
        AggregationResults<DateWiseShiftResponse> shiftData = mongoTemplate.aggregate(aggregation, Shift.class, DateWiseShiftResponse.class);
        return shiftData.getMappedResults();

    }

    public void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("staffId").is(staffId).and("startDate").gt(employmentEndDate));
        Update update = new Update();
        update.set("deleted", true);

        mongoTemplate.updateMulti(query, update, Shift.class);

    }

   /* public List<ShiftTimeDTO> getShiftTimeDTO(List<FilterCriteria> filters){
        ShiftFilterCriteria shiftFilterCriteria = ShiftFilterCriteria.getInstance();
        for(FilterCriteria filter: filters){
            switch (filter.getType()){
                case STAFF_IDS: shiftFilterCriteria.setStaffIds(filter.getValues()); break;
                case ACTIVITY_IDS: shiftFilterCriteria.setActivityIds(filter.getValues()); break;
                case UNIT_IDS: shiftFilterCriteria.setUnitId(filter.getValues()); break;
                case TIME_INTERVAL: shiftFilterCriteria.setTimeInterval(filter.getValues()); break;
                default: break;
            }
        }
        List<AggregationOperation> aggregationOperations = shiftFilterCriteria.getMatchOperations();
        //aggregationOperations.add(Aggregation.group(""))
        return new ArrayList<>();
    }*/
}
