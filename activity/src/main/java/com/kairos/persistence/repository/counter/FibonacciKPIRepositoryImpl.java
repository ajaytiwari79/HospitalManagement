package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.fibonacci_kpi.FibonacciKPIDTO;
import com.kairos.persistence.model.counter.FibonacciKPI;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.regex.Pattern;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * pradeep
 * 16/4/19
 */
public class FibonacciKPIRepositoryImpl implements CustomFibonacciKPIRepository{

    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public FibonacciKPIDTO getOneByfibonacciId(BigInteger fibonacciId) {
        Criteria criteria = Criteria.where("deleted").is(false).and("_id").is(fibonacciId);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "fibonacciKPIConfigs.kpiId", "_id", "counter")
        );
        AggregationResults<FibonacciKPIDTO> results = mongoTemplate.aggregate(aggregation, FibonacciKPI.class, FibonacciKPIDTO.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }

    @Override
    public boolean existByName(BigInteger fibonacciId,String title,ConfLevel confLevel,Long referenceId) {
        Criteria criteria = Criteria.where("deleted").is(false).and("confLevel").is(confLevel).and("referenceId").is(referenceId).and("title").regex(Pattern.compile("^" + title + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(fibonacciId)){
            criteria.and("_id").ne(fibonacciId);
        }
        return mongoTemplate.exists(new Query(criteria), FibonacciKPI.class);
    }


}
