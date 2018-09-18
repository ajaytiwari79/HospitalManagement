package com.kairos.persistence.repository.filter;

import com.kairos.enums.FilterType;
import com.kairos.persistence.model.filter.FilterGroup;
import com.kairos.response.dto.filter.FilterCategoryResult;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.List;
import java.util.Map;

public interface CustomFilterMongoRepository {


    Map<String, AggregationOperation> getFilterCriteria(Long countryId,Long organizationId,List<FilterType> filterTypes,FilterGroup  filterGroup);

    void buildAggregationQuery(FilterType filterType, Map<String, AggregationOperation> aggregationOperations );

    Aggregation createAggregationQueryForFilterCategory(Map<String, AggregationOperation> aggregationOperations);

    AggregationResults<FilterCategoryResult> getFilterAggregationResult(Aggregation aggregation, FilterGroup filterGroup, String moduleId);

}
