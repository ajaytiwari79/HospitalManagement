package com.kairos.activity.persistence.repository.night_worker;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerMongoRepositoryImpl implements CustomNightWorkerMongoRepository{

    @Inject
    MongoTemplate mongoTemplate;

    /*public  boolean getNightWorkerGeneralDetails(Long staffId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(staffId)),
                unwind("staffQuestionnaires", true),
                lookup("staffQuestionnaire", "tags", "_id", "tags_data"),
                lookup("phases", "currentPhaseId", "_id", "current_phase_data"),
                match(Criteria.where("current_phase_data.sequence").ne(sequence)), count().as("countOfPhasesWithOtherSequence")
        );

        AggregationResults<Map> result =
                mongoTemplate.aggregate(aggregation, "planningPeriod", Map.class);
        Map resultData = result.getUniqueMappedResult();
        if (Optional.ofNullable(resultData).isPresent()) {
            return (Integer) result.getUniqueMappedResult().get("countOfPhasesWithOtherSequence") > 0;
        } else {
            return false;
        }
    }*/
}
