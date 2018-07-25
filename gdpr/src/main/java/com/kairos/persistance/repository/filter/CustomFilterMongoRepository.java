package com.kairos.persistance.repository.filter;

import com.kairos.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;
import java.util.Map;

public interface CustomFilterMongoRepository {


    Map<String, AggregationOperation> getFilterCriteria(Long countryId,Long organizationId,List<FilterType> filterTypes,FilterGroup  filterGroup);

    AggregationOperation buildAggregationQuery(FilterType filterType);

    Aggregation createAggregationQueryForFilterCategory(Map<String, AggregationOperation> aggregationOperations);

    AggregationResults<FilterQueryResult> getFilterAggregationResult(Aggregation aggregation,FilterGroup filterGroup, String moduleId);

}
