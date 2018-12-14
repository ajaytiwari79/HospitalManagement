package com.kairos.persistence.repository.time_type;

import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.counter.AccessGroupKPIEntry;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 23/10/18
 **/
public class TimeTypeMongoRepositoryImpl implements CustomTimeTypeMongoRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Set<BigInteger> findActivityIdsByTimeTypeIds(List<BigInteger> timeTypeIds) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where("id").in(timeTypeIds)),
                Aggregation.graphLookup("time_Type").startWith("id").connectFrom("_id").connectTo("upperLevelTimeTypeId").as("children"),
                Aggregation.project("children._id"),
                Aggregation.unwind("_id"))
            ;
            AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,TimeType.class,Map.class);
            return results.getMappedResults().stream().map(a->new BigInteger(a.get("_id").toString())).collect(Collectors.toSet());
    }

    @Override
    public Set<BigInteger> findActivityIdssByTimeTypeEnum(List<String> timeTypeEnums) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timeTypes").in(timeTypeEnums)),
                Aggregation.project("_id"),
                Aggregation.unwind("_id"));
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,TimeType.class,Map.class);
        return results.getMappedResults().stream().map(s-> new BigInteger(s.get("_id").toString())).collect(Collectors.toSet());
    }
}
