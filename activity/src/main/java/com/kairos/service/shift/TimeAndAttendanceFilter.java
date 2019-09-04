package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.TA_STATUS;

public class TimeAndAttendanceFilter implements ShiftFilter {



    private Map<FilterType, Set<String>> filterCriteriaMap;
    private List<BigInteger> shiftStateIds;

    public TimeAndAttendanceFilter(Map<FilterType, Set<String>> filterCriteriaMap,List<BigInteger> shiftStateIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.shiftStateIds=shiftStateIds;

    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TA_STATUS) && isCollectionNotEmpty(filterCriteriaMap.get(TA_STATUS));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if (validFilter) {
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if (shiftStateIds.contains(shiftDTO.getId()) || filterCriteriaMap.get(TA_STATUS).contains(String.valueOf(shiftDTO.getAccessGroupRole()))) {
                    filteredShifts.add((T) shiftDTO);
                }
            }
        }
            return filteredShifts;
        }

}
