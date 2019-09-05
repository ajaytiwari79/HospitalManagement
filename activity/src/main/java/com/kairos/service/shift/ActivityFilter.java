package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.ABSENCE_ACTIVITY;

public class ActivityFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;


    public ActivityFilter(Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;

    }


    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterCriteriaMap.get(ABSENCE_ACTIVITY));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            List<BigInteger> activityIds=filterCriteriaMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s)).collect(Collectors.toList());
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(shiftDTO.getActivities().stream().anyMatch(shiftActivityDTO -> activityIds.contains(shiftActivityDTO.getActivityId())))
                filteredShifts.add((T)shiftDTO);
            }

        }
        return filteredShifts;
    }
}
