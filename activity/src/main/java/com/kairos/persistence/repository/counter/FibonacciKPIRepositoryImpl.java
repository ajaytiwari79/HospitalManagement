package com.kairos.persistence.repository.counter;

import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.counter.ApplicableKPI;
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
import static com.kairos.persistence.repository.counter.CounterRepository.getRefQueryField;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * pradeep
 * 16/4/19
 */
public class FibonacciKPIRepositoryImpl implements CustomFibonacciKPIRepository{

    private static final String TITLE = "title";
    @Inject
    private MongoTemplate mongoTemplate;


    @Override
    public KPIDTO getOneByfibonacciId(BigInteger fibonacciId,Long referenceId,ConfLevel confLevel) {
        Criteria criteria = Criteria.where("deleted").is(false).and("activeKpiId").is(fibonacciId).and("level").is(confLevel).and(getRefQueryField(confLevel)).is(referenceId);
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "activeKpiId", "_id", "kpi"),
                project(TITLE,"fibonacciKPIConfigs").and("kpi").arrayElementAt(0).as("kpi"),
                match(Criteria.where("kpi.fibonacciKPI").is(true)),
                project(TITLE,"fibonacciKPIConfigs").and("kpi._id").as("_id").and("kpi.type").as("type")
                        .and("kpi.calculationFormula").as("calculationFormula").and("kpi.counter").as("counter").
                        and("kpi.fibonacciKPI").as("fibonacciKPI").and("kpi.description").as("kpi.description")
                        .and("kpi.referenceId").as("referenceId")
        );
        AggregationResults<KPIDTO> results = mongoTemplate.aggregate(aggregation, ApplicableKPI.class, KPIDTO.class);
        return results.getMappedResults().isEmpty() ? null : results.getMappedResults().get(0);
    }

    @Override
    public boolean existByName(BigInteger fibonacciId,String title,ConfLevel confLevel,Long referenceId) {
        Criteria criteria = Criteria.where("deleted").is(false).and("confLevel").is(confLevel).and("referenceId").is(referenceId).and(TITLE).regex(Pattern.compile("^" + title + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(fibonacciId)){
            criteria.and("_id").ne(fibonacciId);
        }
        return mongoTemplate.exists(new Query(criteria), FibonacciKPI.class);
    }


}
