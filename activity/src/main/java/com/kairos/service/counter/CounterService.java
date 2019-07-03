package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.kpi.KPIResponseDTO;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.Direction;
import com.kairos.persistence.model.counter.*;

import java.util.*;
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
    CommonRepresentationData getCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi);

    CommonRepresentationData getCalculatedKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI);

    KPIResponseDTO getCalculatedDataOfKPI(Map<FilterType, List> filterBasedCriteria, Long organizationId, KPI kpi, ApplicableKPI applicableKPI);

    TreeSet<FibonacciKPICalculation> getFibonacciCalculatedCounter(Map<FilterType, List> filterBasedCriteria, Long organizationId, Direction sortingOrder, List<StaffKpiFilterDTO> staffKpiFilterDTOS, ApplicableKPI applicableKPI);
}
