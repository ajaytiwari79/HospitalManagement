package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.PLANNED_BY;

public class PlannedByFilter <G> implements ShiftFilter{

    private Set<Long> plannedByUserIds;
    private Map<FilterType, Set<G>> filterCriteriaMap;

    public PlannedByFilter(Set<Long> plannedByUserIds, Map<FilterType, Set<G>> filterCriteriaMap) {
        this.plannedByUserIds = plannedByUserIds;
        this.filterCriteriaMap = filterCriteriaMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(PLANNED_BY));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(isNotNull(shiftDTO.getCreatedBy()) && plannedByUserIds.contains(shiftDTO.getCreatedBy().getId())){
                    filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
