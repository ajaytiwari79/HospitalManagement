package com.kairos.service.counter;

import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.data.RawRepresentationData;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.Counter;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.counter.CounterRepository;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Service
public class ScheduledHoursService implements CounterService {

    @Inject
    CounterRepository counterRepository;
    @Inject
    CounterFilterService counterFilterService;

    private Map getApplicableCriteria(Map availableCriteria){
        Counter counter = counterRepository.getCounterByType(CounterType.SCHEDULED_HOURS_NET);
        Map<FilterType, List> applicableCriteria = getApplicableFilters(counter.getCriteriaList(), availableCriteria);
        return applicableCriteria;
    }

    //TODO: Implimantation is pending as functionality chnaged.
    private int calculateData(Map applicableCriteria){
        List<AggregationOperation> operations = counterFilterService.getShiftFilterCriteria(applicableCriteria);
        List<Shift> shifts = counterRepository.getMappedValues(operations, Shift.class, ShiftDTO.class);
        //shifts.stream().map((shift.getEndDate().getTime(),shift.getStartDate().getTime()) -> }).collect(Collectors.toList())

        shifts.forEach(shift -> { });
        return 0;
    }

    @Override
    public RawRepresentationData getCalculatedCounter(Map filterBasedCriteria, Long countryId, KPI kpi) {
        return null;
    }

    @Override
    public RawRepresentationData getCalculatedKPI(Map filterBasedCriteria, Long countryId, KPI kpi) {
        return null;
    }
}
