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
 * Created By G.P.Ranjan on 10/12/19
 **/
public class GroupFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Set<Long> groupMembers;

    public GroupFilter(Set<Long> groupMembers, Map<FilterType, Set<G>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.groupMembers = groupMembers;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(GROUPS) && isCollectionNotEmpty(filterCriteriaMap.get(GROUPS));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(groupMembers.contains(shiftDTO.getStaffId())){
                    filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
