package com.kairos.activity.persistence.repository.night_worker;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerMongoRepositoryImpl implements CustomNightWorkerMongoRepository{

    @Inject
    MongoTemplate mongoTemplate;

    /*public  boolean checkIfPeriodsByStartAndEndDateExistInPhaseExceptGivenSequence(Long unitId, Date startDate, Date endDate, int sequence) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId)
                        .orOperator(
                                Criteria.where("startDate").gte(startDate).lte(endDate),
                                Criteria.where("endDate").gte(startDate).lte(endDate)
                        )),
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
