package com.kairos.service.filter;


import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.ModuleIdDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.repository.filter.FilterGroupMongoRepository;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilterService {


    @Inject
    private FilterGroupMongoRepository filterGroupMongoRepository;


  /*  public FilterGroup addFilterGroup(FilterGroup filterGroup, String moduleId) {
        List<ModuleIdDto> moduleIdDtos = filterGroup.getAccessModule();
        List<String> moduleids = new ArrayList<>();
        moduleIdDtos.forEach(moduleIdDto -> moduleids.add(moduleIdDto.getModuleId()));

        List<FilterGroup> filterGroups = filterGroupMongoRepository.findFilterGroupByModuleIds(moduleids, true);
        if (filterGroups.size() != 0) {



        }


    }
*/

    public Map<String, AggregationOperation> getFilterCriterias(Long countryId, List<FilterType> filterTypes) {


        Map<String, AggregationOperation> aggregationOperations = new HashMap<>();
        aggregationOperations.put("match", match(Criteria.where("countryId").is(countryId).and("deleted").is(false)));
        filterTypes.forEach(filterType -> {
                    aggregationOperations.put(filterType.value, buildAggregationQuery(filterType));
                }

        );

        return aggregationOperations;
    }


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


}

