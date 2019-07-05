package com.kairos.persistence.repository.time_type;

import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.persistence.model.activity.TimeType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CreatedBy vipulpandey on 23/10/18
 **/
public class TimeTypeMongoRepositoryImpl implements CustomTimeTypeMongoRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    @Override
    public Set<BigInteger> findAllTimeTypeIdsByTimeTypeIds(List<BigInteger> timeTypeIds) {
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
    public Set<BigInteger> findTimeTypeIdssByTimeTypeEnum(List<String> timeTypeEnums) {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timeTypes").in(timeTypeEnums)),
                Aggregation.project("id"),
                Aggregation.unwind("id"));
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,TimeType.class,Map.class);
        return results.getMappedResults().stream().map(s-> new BigInteger(s.get("_id").toString())).collect(Collectors.toSet());
    }

    @Override
    public List<TimeTypeDTO> findTimeTypeWithItsParent() {
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(Criteria.where("deleted").is(false)),
                Aggregation.graphLookup("time_Type").startWith("upperLevelTimeTypeId").connectFrom("upperLevelTimeTypeId").connectTo("_id").as("parent"));
        AggregationResults<TimeTypeDTO> results = mongoTemplate.aggregate(aggregation,TimeType.class,TimeTypeDTO.class);
        return results.getMappedResults();
    }

}
