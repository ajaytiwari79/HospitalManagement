package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.enums.FilterType;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class CounterFilterService {

    //to manage a map of all filters map
    public Map<FilterType, List> getFiltersMap(List<FilterCriteria> filters){
        return filters.stream().collect(Collectors.toMap(filter-> filter.getType(), filter-> filter.getValues()));
    }

    //TODO: to be implemented
//    public AggregationOperation getFilteredActivities(Map<FilterType, List> filtersMap){
//        ActivityFilterCriteria activityCriteria = ActivityFilterCriteria.getInstance();
//        return activityCriteria
//                .setTimeTypeList(filtersMap.get(FilterType.TIME_TYPE))
//                .setEmploymentTypes(filtersMap.get(FilterType.EMPLOYMENT_TYPE))
//                .setActivityIds(null)
//                .setOrganizationTypes(filtersMap.get(FilterType.ORGANIZATION_TYPE))
//                .setPlanneTimeType(filtersMap.get(FilterType.PLANNED_TIME_TYPE))
//                .setUnitId(null)
//                .setCategoryId(filtersMap.get(FilterType.ACTIVITY_CATEGORY_TYPE))
//                .setExpertiseCriteria(filtersMap.get(FilterType.EXPERTISE))
//                .getFilterCriteria();
//    }

    public List<AggregationOperation> getShiftFilterCriteria(Map<FilterType, List> filtersMap){
        return null;//ShiftFilterCriteria
        //        .getInstance()
//                .setTimeTypeList(filtersMap.get(FilterType.TIME_TYPE))
//                .setEmploymentTypes(filtersMap.get(FilterType.EMPLOYMENT_TYPE))
//                .setOrganizationTypes(filtersMap.get(FilterType.ORGANIZATION_TYPE))
//                .setPlanneTimeType(filtersMap.get(FilterType.PLANNED_TIME_TYPE))
//                .setUnitId(filtersMap.get(FilterType.UNIT_IDS))
//                .setCategoryId(filtersMap.get(FilterType.ACTIVITY_CATEGORY_TYPE))
//                .setExpertiseCriteria(filtersMap.get(FilterType.EXPERTISE))
//                .setActivityIds(filtersMap.get(FilterType.ACTIVITY_IDS))
//                .setStaffIds(filtersMap.get(FilterType.STAFF_IDS))
//                .setTimeInterval(filtersMap.get(FilterType.TIME_INTERVAL))
//                .getMatchOperations();
    }
}
