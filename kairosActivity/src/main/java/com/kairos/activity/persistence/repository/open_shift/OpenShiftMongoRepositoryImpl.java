package com.kairos.activity.persistence.repository.open_shift;

import com.kairos.activity.persistence.model.open_shift.OpenShift;


import com.kairos.response.dto.web.open_shift.OpenShiftResponseDTO;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by vipul on 14/5/18.
 */
public class OpenShiftMongoRepositoryImpl implements CustomOpenShiftMongoRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    public List<OpenShift> getOpenShiftsByUnitIdAndSelectedDate(Long unitId, Date selectedDate,Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").gte(selectedDate).and("endDate").lt(endDate)));
        AggregationResults<OpenShift> result = mongoTemplate.aggregate(aggregation, OpenShift.class, OpenShift.class);
        return result.getMappedResults();
    }
}

