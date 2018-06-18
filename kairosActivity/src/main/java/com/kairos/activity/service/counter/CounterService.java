package com.kairos.activity.service.counter;

import com.kairos.activity.persistence.model.counter.FilterCriteria;
import com.kairos.persistence.model.enums.FilterType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map getCalculatedResults(Map<FilterType, List> providedFiltersMap);
}
