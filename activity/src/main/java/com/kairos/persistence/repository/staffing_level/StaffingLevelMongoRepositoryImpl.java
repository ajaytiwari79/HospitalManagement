package com.kairos.persistence.repository.staffing_level;

import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Component
public class StaffingLevelMongoRepositoryImpl implements StaffingLevelCustomRepository{
    public static final String CURRENT_DATE = "currentDate";
    @Autowired
    private MongoTemplate mongoTemplate;


    public List<StaffingLevel> getStaffingLevelsByUnitIdAndDate(Long unitId, Date startDate, Date endDate){
        Query query = new Query(Criteria.where("unitId").is(unitId).and(CURRENT_DATE).gte(startDate).lte(endDate).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC, CURRENT_DATE));
        return mongoTemplate.find(query,StaffingLevel.class);
    }

    @Override
    public List<PresenceStaffingLevelDto> findByUnitIdAndDatesAndActivityId(Long unitId, Date startDate, Date endDate, BigInteger activityId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and(CURRENT_DATE).gte(startDate).lte(endDate)),
                Aggregation.unwind("presenceStaffingLevelInterval"),
                new CustomAggregationOperation(Document.parse("{$addFields: { \"presenceStaffingLevelInterval.activityIds\":\"$presenceStaffingLevelInterval.staffingLevelActivities.activityId\"}}")),
                new CustomAggregationOperation(Document.parse("{\n" +
                        "      $project: {\n" +
                        "          \"staffingLevelSetting\":1,\n" +
                        "          \"presenceStaffingLevelInterval\":1,\n" +
                        "          \"currentDate\":1,\n" +
                        "         staffingLevelActivities: {\n" +
                        "            $filter: {\n" +
                        "               input: \"$presenceStaffingLevelInterval.staffingLevelActivities\",\n" +
                        "               as: \"staffingLevelActivity\",\n" +
                        "               cond: { $eq: [ \"$$staffingLevelActivity.activityId\", "+activityId+"] }\n" +
                        "            }\n" +
                        "         }\n" +
                        "      }\n" +
                        "   }")),
                new CustomAggregationOperation(Document.parse("{$addFields: {\"presenceStaffingLevelInterval.activities\":\"$staffingLevelActivities\"}}")),
                Aggregation.group("currentDate","staffingLevelSetting").push("presenceStaffingLevelInterval").as("presenceStaffingLevelInterval"),
                Aggregation.project("presenceStaffingLevelInterval").andExclude("_id").and("_id.currentDate").as(CURRENT_DATE).and("_id.staffingLevelSetting").as("staffingLevelSetting")
        );
        return mongoTemplate.aggregate(aggregation,StaffingLevel.class, PresenceStaffingLevelDto.class).getMappedResults();
    }


}
