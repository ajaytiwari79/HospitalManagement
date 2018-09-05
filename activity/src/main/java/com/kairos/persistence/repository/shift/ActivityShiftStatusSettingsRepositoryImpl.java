package com.kairos.persistence.repository.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityShiftStatusSettingsRepositoryImpl implements CustomActivityShiftStatusSettingsRepository {
    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long unitId, BigInteger activityId) {
        Aggregation aggregation=newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("activityId").is(activityId)),
                group("shiftStatus").push("$$ROOT").as("activityAndShiftStatusSettings"),
                project("activityAndShiftStatusSettings").and("_id").as("status"));
        AggregationResults<ActivityAndShiftStatusWrapper> result=mongoTemplate.aggregate(aggregation,ActivityShiftStatusSettings.class,ActivityAndShiftStatusWrapper.class);
        return result.getMappedResults();
    }
}
