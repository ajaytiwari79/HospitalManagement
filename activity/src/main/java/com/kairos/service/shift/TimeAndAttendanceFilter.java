package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.VALIDATED_BY;

public class TimeAndAttendanceFilter <G> implements ShiftFilter {



    private Map<FilterType, Set<G>> filterCriteriaMap;
    private List<BigInteger> shiftStateIds;

    public TimeAndAttendanceFilter(Map<FilterType, Set<G>> filterCriteriaMap,List<BigInteger> shiftStateIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.shiftStateIds=shiftStateIds;

    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(VALIDATED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(VALIDATED_BY));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if (validFilter) {
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if (shiftStateIds.contains(shiftDTO.getId()) || filterCriteriaMap.get(VALIDATED_BY).contains(String.valueOf(shiftDTO.getAccessGroupRole()))) {
                    filteredShifts.add((T) shiftDTO);
                }
            }
        }
            return filteredShifts;
        }

}
