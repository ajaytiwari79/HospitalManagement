package com.kairos.persistence.repository.staff_settings;

import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

public class StaffActivitySettingRepositoryImpl implements StaffActivitySettingRepositoryCustom {

    @Inject private MongoTemplate mongoTemplate;

    public List<ActivityWithCompositeDTO> findAllStaffActivitySettingByStaffIdAndUnityIdWithMostUsedActivityCount(Long unitId, Long staffId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("staffId").is(staffId).and("unitId").is(unitId).and("deleted").is(false)),
                new CustomAggregationOperation(Document.parse("{\n" +
                        "      $lookup:\n" +
                        "         {\n" +
                        "           from: \"staffActivityDetails\",\n" +
                        "           let: { staffId: \"$staffId\", activityId: \"$activityId\" },\n" +
                        "           pipeline: [\n" +
                        "              { $match:\n" +
                        "                 { $expr:\n" +
                        "                    { $and:\n" +
                        "                       [\n" +
                        "                         { $eq: [ \"$staffId\",  \"$$staffId\" ] },\n" +
                        "                         { $gte: [ \"$activityId\", \"$$activityId\" ] }\n" +
                        "                       ]\n" +
                        "                    }\n" +
                        "                 }\n" +
                        "              },\n" +
                        "              { $project: { useActivityCount: 1, _id: 0 } },\n" +
                        "              \n" +
                        "           ],\n" +
                        "           as: \"useActivityCount\"\n" +
                        "         }\n" +
                        "    }")),
                new CustomAggregationOperation(Document.parse("{ \"$addFields\": {\n" +
                        "        \"mostlyUsedCount\": {\n" +
                        "            \"$arrayElemAt\": [ \"$useActivityCount.useActivityCount\", 0 ]\n" +
                        "        }\n" +
                        "    }}"))
        );
        return mongoTemplate.aggregate(aggregation, StaffActivitySetting.class,ActivityWithCompositeDTO.class).getMappedResults();
    }
}
