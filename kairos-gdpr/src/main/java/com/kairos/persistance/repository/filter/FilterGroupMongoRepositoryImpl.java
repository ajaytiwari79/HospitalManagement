package com.kairos.persistance.repository.filter;

import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.enums.FilterType;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

public class FilterGroupMongoRepositoryImpl implements CustomeFilterMongoRepository {

    @Override
    public Map<String, AggregationOperation> getFilterCriterias(Long countryId, List<FilterType> filterTypes) {
        Map<String, AggregationOperation> aggregationOperations = new HashMap<>();
        aggregationOperations.put("match", match(Criteria.where("countryId").is(countryId).and("deleted").is(false)));
        filterTypes.forEach(filterType -> {
                    aggregationOperations.put(filterType.value, buildAggregationQuery(filterType));
                }

        );

        return aggregationOperations;
    }

    @Override
    public AggregationOperation buildAggregationQuery(FilterType filterType) {
        switch (filterType) {

            case ACCOUNT_TYPES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SERVICES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SUB_SERVICES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_TYPES:
                return Aggregation.unwind(filterType.value);
            case ORGANIZATION_SUB_TYPES:
                return Aggregation.unwind(filterType.value);
            default:
                throw new InvalidRequestException("invalid request");
        }


    }

    @Override
    public Aggregation createAggregationQueryForMasterAsset(Map<String, AggregationOperation> aggregationOperations) {
        GroupOperation groupOperation = group();
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(aggregationOperations.get("match"));
        for (Map.Entry<String, AggregationOperation> entry : aggregationOperations.entrySet())
            if (entry.getKey().equals("match")) {
                continue;
            } else {
                operations.add(entry.getValue());
                groupOperation = groupOperation.addToSet(entry.getKey()).as(entry.getKey());
            }
        operations.add(groupOperation);
        Aggregation aggregation = Aggregation.newAggregation(operations);
        return aggregation;

    }
}
