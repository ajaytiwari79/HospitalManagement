package com.kairos.persistence.repository.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusWrapper;
import com.kairos.persistence.model.shift.ActivityAndShiftStatusSettings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityAndShiftStatusSettingsRepositoryImpl implements CustomActivityAndShiftStatusSettingsRepository {
    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long countryId) {
        Aggregation aggregation=newAggregation(
               match(Criteria.where("countryId").is(countryId).and("deleted").is(false)),
                group("shiftStatus").push("$$ROOT").as("activityAndShiftStatusSettings"),
                project("activityAndShiftStatusSettings").and("_id").as("status"));
        AggregationResults<ActivityAndShiftStatusWrapper> result=mongoTemplate.aggregate(aggregation,ActivityAndShiftStatusSettings.class,ActivityAndShiftStatusWrapper.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatusForUnit(Long unitId) {
        Aggregation aggregation=newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                group("shiftStatus").push("$$ROOT").as("activityAndShiftStatusSettings"),
                project("activityAndShiftStatusSettings").and("_id").as("status"));
        AggregationResults<ActivityAndShiftStatusWrapper> result=mongoTemplate.aggregate(aggregation,ActivityAndShiftStatusSettings.class,ActivityAndShiftStatusWrapper.class);
        return result.getMappedResults();
    }
}
