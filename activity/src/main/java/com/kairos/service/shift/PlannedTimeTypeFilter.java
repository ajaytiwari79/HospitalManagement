package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.PLANNED_TIME_TYPE;

public class PlannedTimeTypeFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;


    public PlannedTimeTypeFilter(Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;

    }
    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(PLANNED_TIME_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(PLANNED_TIME_TYPE));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            List<BigInteger> plannedTimeTypeIds=filterCriteriaMap.get(PLANNED_TIME_TYPE).stream().map(s -> new BigInteger(s)).collect(Collectors.toList());
            for (ShiftDTO shiftDTO : shiftDTOS) {
                List<BigInteger> shiftPlannedTimeTypeIds= shiftDTO.getActivities().stream().flatMap(shiftActivityDTO -> shiftActivityDTO.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId())).collect(Collectors.toList());
                if(CollectionUtils.containsAny(plannedTimeTypeIds,shiftPlannedTimeTypeIds))
                    filteredShifts.add((T)shiftDTO);
            }
        }
        return filteredShifts;
    }
}
