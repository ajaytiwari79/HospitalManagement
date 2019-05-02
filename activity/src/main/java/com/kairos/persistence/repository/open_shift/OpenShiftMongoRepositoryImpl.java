package com.kairos.persistence.repository.open_shift;

import com.kairos.persistence.model.open_shift.OpenShift;
import com.kairos.persistence.model.open_shift.OpenShiftActivityWrapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by vipul on 14/5/18.
 */
public class OpenShiftMongoRepositoryImpl implements CustomOpenShiftMongoRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    public List<OpenShift> getOpenShiftsByUnitIdAndDate(Long unitId, Date startDate, Date endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("startDate").gte(startDate).and("endDate").lte(endDate)));
        AggregationResults<OpenShift> result = mongoTemplate.aggregate(aggregation, OpenShift.class, OpenShift.class);
        return result.getMappedResults();
    }

   public OpenShiftActivityWrapper getOpenShiftAndActivity(BigInteger openShiftId, Long unitId){
        Aggregation aggregation=Aggregation.newAggregation(
                match(Criteria.where("_id").is(openShiftId).and("unitId").is(unitId).and("deleted").is(false)),
                        lookup("activities","activityId","_id","activity"),
                lookup("order","orderId","_id","order"),
                project().and("order.expertiseId").arrayElementAt(0).as("expertiseId").and("activity").arrayElementAt(0).as("activity"));
        AggregationResults<OpenShiftActivityWrapper> result=mongoTemplate.aggregate(aggregation,OpenShift.class,OpenShiftActivityWrapper.class);
        return result.getMappedResults().isEmpty() ? null : result.getMappedResults().get(0);

   }
}

