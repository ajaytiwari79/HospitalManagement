package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.persistence.model.counter.ApplicableKPI;
import com.kairos.persistence.model.counter.FibonacciKPI;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * pradeep
 * 16/4/19
 */
public class FibonacciKPIRepositoryImpl implements CustomFibonacciKPIRepository{

    @Inject
    private MongoTemplate mongoTemplate;


    public List<FibonacciKPIDTO> getOneByfibonacciId(BigInteger fibonacciId) {
        Criteria criteria = Criteria.where("deleted").is(false).and("_id").is(fibonacciId);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "fibonacciKPIConfigs.kpiId", "_id", "counter")
        );
        AggregationResults<FibonacciKPIDTO> results = mongoTemplate.aggregate(aggregation, FibonacciKPI.class, FibonacciKPIDTO.class);
        return results.getMappedResults();
    }


}
