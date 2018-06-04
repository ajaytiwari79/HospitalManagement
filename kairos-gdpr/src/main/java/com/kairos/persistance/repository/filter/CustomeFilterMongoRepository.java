package com.kairos.persistance.repository.filter;

import com.kairos.persistance.model.enums.FilterType;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;
import java.util.Map;

public interface CustomeFilterMongoRepository {


    Map<String, AggregationOperation> getFilterCriterias(Long countryId, List<FilterType> filterTypes);

    AggregationOperation buildAggregationQuery(FilterType filterType);

    Aggregation createAggregationQueryForMasterAsset(Map<String, AggregationOperation> aggregationOperations);

}
