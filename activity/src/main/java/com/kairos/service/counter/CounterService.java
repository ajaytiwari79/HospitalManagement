package com.kairos.service.counter;


import com.kairos.activity.counter.data.BasicRequirementDTO;
import com.kairos.activity.counter.data.FilterCriteria;
import com.kairos.activity.counter.data.FilterCriteriaDTO;
import com.kairos.activity.counter.data.RepresentationDTO;
import com.kairos.enums.FilterType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public interface CounterService {

    default Map<FilterType, List> getApplicableFilters(List<FilterCriteria> availableFilters, Map<FilterType, List> providedFiltersMap){
        Map<FilterType, List> applicableCriteria = new HashMap<>();
        availableFilters.forEach(filterCriteria -> {
            List providedType = providedFiltersMap.get(filterCriteria.getType());
            List applicableFilters = providedType.isEmpty()
                    ?filterCriteria.getValues()
                    :filterCriteria.getValues().stream().map(value -> providedType.contains(value)).collect(Collectors.toList());
            applicableCriteria.put(filterCriteria.getType(), applicableFilters);
        });
        providedFiltersMap.forEach((type, list)->{
            if(applicableCriteria.get(type) == null)
                applicableCriteria.put(type, list);
        });
        return applicableCriteria;
    }

    //map -> { data: [ {} ] }
    RepresentationDTO getCalculatedCounter(FilterCriteriaDTO filterCriteria, BasicRequirementDTO representationRequirement);
}
