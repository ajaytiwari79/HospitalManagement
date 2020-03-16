package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.*;

/**
 * Created By G.P.Ranjan on 11/3/20
 **/
public class AssignActivityFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private List<Long> assignActivityStaffIds;

    public AssignActivityFilter(Map<FilterType, Set<G>> filterCriteriaMap, List<Long> assignActivityStaffIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.assignActivityStaffIds = assignActivityStaffIds;
    }


    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = (filterCriteriaMap.containsKey(ASSIGN_ACTIVITY) && isCollectionNotEmpty(filterCriteriaMap.get(ASSIGN_ACTIVITY))) || (filterCriteriaMap.containsKey(TEAM) && isCollectionNotEmpty(filterCriteriaMap.get(TEAM)));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(assignActivityStaffIds.contains(shiftDTO.getStaffId())){
                    filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
