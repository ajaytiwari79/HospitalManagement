package com.kairos.service.clause;


import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.service.filter.FilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

@Service
public class ClauseFilterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClauseFilterService.class);

@Inject
private FilterGroupMongoRepository filterGroupMongoRepository;

@Inject
private FilterService filterService;

@Inject
private MongoTemplate mongoTemplate;

    public FilterQueryResult FilterQueryResult(Long countryId, String moduleId) {

        Map<String, AggregationOperation> filterCriteria = new HashMap<>();
        FilterGroup filterGroup = filterGroupMongoRepository.findFilterGroupByModuleId(moduleId,true);
        if (Optional.ofNullable(filterGroup).isPresent()) {
            List<FilterType> filterTypes = filterGroup.getFilterTypes();
            filterCriteria = filterService.getFilterCriterias(countryId, filterTypes);
            Aggregation aggregation = createAggregationQueryForMasterAsset(filterCriteria);
            AggregationResults<FilterQueryResult> result = mongoTemplate.aggregate(aggregation, Clause.class, FilterQueryResult.class);
            return result.getUniqueMappedResult();

        }
        return null;

    }


    public Aggregation createAggregationQueryForMasterAsset(Map<String, AggregationOperation> aggregationOperations) {



        GroupOperation groupOperation=group();
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(aggregationOperations.get("match"));
        for (Map.Entry<String, AggregationOperation> entry : aggregationOperations.entrySet())
            if (entry.getKey().equals("match")) {
                continue;
            } else {
                operations.add(entry.getValue());
                groupOperation= groupOperation.addToSet(entry.getKey()).as(entry.getKey());
                System.err.println(entry.getKey());
            }
        operations.add(groupOperation);
        Aggregation aggregation = Aggregation.newAggregation(operations);
        return aggregation;

    }



}
