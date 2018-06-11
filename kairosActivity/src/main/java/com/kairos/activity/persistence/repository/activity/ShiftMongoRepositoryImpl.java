package com.kairos.activity.persistence.repository.activity;

import com.kairos.activity.persistence.model.activity.Shift;

import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.response.dto.web.ShiftCountDTO;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by vipul on 22/9/17.
 */
public class ShiftMongoRepositoryImpl implements CustomShiftMongoRepository {

    private static final Logger logger = LoggerFactory.getLogger(ShiftMongoRepositoryImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updatePhasesOfActivities(Long orgId, Date startDateInISO, Date endDateInISO, String phaseName, String PhaseDescription) {

        Query query = new Query();
        query.addCriteria(Criteria.where("disabled").is(false).and("unitId").is(orgId).and("startDate").gte(startDateInISO).and("endDate").lte(endDateInISO));
        Update update = new Update();
        update.set("phase.name", phaseName);
        update.set("phase.description", PhaseDescription);


        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Shift.class);

        logger.info(query.toString() + " " + updateResult.toString());

    }

    public List<ShiftQueryResult> findAllShiftsBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("unitPositionId").is(unitPositionId).and("deleted").is(false).and("staffId").is(staffId).and("isMainShift").is(true)
                        .and("startDate").gte(startDate).and("endDate").lte(endDate)),
                graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShifts"));
        AggregationResults<ShiftQueryResult> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftQueryResult.class);
        return result.getMappedResults();
    }

    public List<ShiftQueryResultWithActivity> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isMainShift").is(true).and("unitPositionId").is(unitEmploymentPositionId)
                        .and("startDate").lte(endDate).and("endDate").gte(startDate)),
                graphLookup("shifts").startWith("$subShifts").connectFrom("subShifts").connectTo("_id").as("subShift"), unwind("subShifts", true),
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
        AggregationResults<ShiftQueryResultWithActivity> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftQueryResultWithActivity.class);
        return result.getMappedResults();
    }

    public List<ShiftQueryResult> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").gte(startDate).and("endDate").lt(endDate)),

                sort(Sort.Direction.ASC, "staffId"));
        AggregationResults<ShiftQueryResult> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftQueryResult.class);
        return result.getMappedResults();
    }

    public List<Long> getUnitIdListOfShiftBeforeDate(Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("endDate").lte(endDate)),
                project().and("unitId").as("unitId"),
                group("unitId"),

                sort(Sort.Direction.ASC, "unitId"));
        AggregationResults<HashMap> result = mongoTemplate.aggregate(aggregation, Shift.class, HashMap.class);
        return (List<Long>) result.getMappedResults().get(0).values();
    }

    public List<ShiftQueryResult> getShiftsByUnitBeforeDate(Long unitId, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("endDate").lte(endDate))
                , project("unitId")
                        .andInclude("startDate")
                        .andInclude("endDate").andInclude("unitPositionId").andInclude("staffId"));
        AggregationResults<ShiftQueryResult> result = mongoTemplate.aggregate(aggregation, Shift.class, ShiftQueryResult.class);
        return result.getMappedResults();
    }


    public List<ShiftCountDTO> getAssignedShiftsCountByUnitPositionId(List<Long> unitPositionIds, Date startDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match( Criteria.where("unitPositionId").in(unitPositionIds).and("startDate").gte(startDate).and("parentOpenShiftId").exists(true)),
                       group("unitPositionId").count().as("count"),
                project("count").and("_id").as("unitPositionId"),
                sort(Sort.Direction.DESC, "count")

        );

        AggregationResults<ShiftCountDTO> shiftCounts = mongoTemplate.aggregate(aggregation,Shift.class,ShiftCountDTO.class);

        return shiftCounts.getMappedResults();

    }

}
