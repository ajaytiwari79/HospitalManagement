package com.kairos.activity.service.counter;

import com.kairos.activity.enums.CounterType;
import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.model.counter.Counter;
import com.kairos.activity.persistence.repository.counter.CounterRepository;
import com.kairos.activity.response.dto.shift.ShiftDTO;
import com.kairos.activity.enums.FilterType;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

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

    private int calculateData(Map applicableCriteria){
        List<AggregationOperation> operations = counterFilterService.getShiftFilterCriteria(applicableCriteria);
        List<Shift> shifts = counterRepository.getMappedValues(operations, Shift.class, ShiftDTO.class);
        //shifts.stream().map((shift.getEndDate().getTime(),shift.getStartDate().getTime()) -> }).collect(Collectors.toList())

        shifts.forEach(shift -> { });
        return 0;
    }
    @Override
    public Map getCalculatedResults(Map<FilterType, List> availableCriteria) {
        Map applicableCriteria = getApplicableCriteria(availableCriteria);

        return null;
    }
}
