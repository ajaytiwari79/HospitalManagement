package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.ABSENCE_ACTIVITY;
import static com.kairos.enums.FilterType.TEAM;

public class ActivityFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private List<BigInteger> selectedActivityIds;

    public ActivityFilter(Map<FilterType, Set<G>> filterCriteriaMap, List<BigInteger> selectedActivityIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.selectedActivityIds = selectedActivityIds;
    }


    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = (filterCriteriaMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterCriteriaMap.get(ABSENCE_ACTIVITY))) || (filterCriteriaMap.containsKey(TEAM) && isCollectionNotEmpty(filterCriteriaMap.get(TEAM)));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(shiftDTO.getActivities().stream().anyMatch(shiftActivityDTO -> selectedActivityIds.contains(shiftActivityDTO.getActivityId())))
                    filteredShifts.add((T)shiftDTO);
            }
        }
        return filteredShifts;
    }
}
