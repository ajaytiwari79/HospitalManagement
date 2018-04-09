package com.kairos.activity.persistence.repository.period;

import com.kairos.activity.persistence.model.period.PlanningPeriod;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Date;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 6/4/18.
 */
public class PlanningPeriodMongoRepositoryImpl implements CustomPlanningPeriodMongoRepository{

    /*public PlanningPeriod findAllActivitiesByOrganizationType(Date startDate) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("startDate").gt(startDate)),
                unwind("phase", true),
                lookup("phase", "currentPhaseId", "_id", "tags_data"),
                unwind("tags_data", true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as("description")
                        .first("$unitId").as("unitId")
                        .first("$parentId").as("parentId")
                        .first("generalActivityTab").as("generalActivityTab")
                        .push("tags_data").as("tags")

        );

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }*/
}
