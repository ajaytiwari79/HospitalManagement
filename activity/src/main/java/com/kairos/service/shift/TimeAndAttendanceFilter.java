package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.ACTIVITY_STATUS;
import static com.kairos.enums.FilterType.TA_STATUS;

public class TimeAndAttendanceFilter implements ShiftFilter {

    @Inject
    private ShiftStateService shiftStateService;

    private Map<FilterType, Set<String>> filterCriteriaMap;


    public TimeAndAttendanceFilter(Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;

    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TA_STATUS) && isCollectionNotEmpty(filterCriteriaMap.get(TA_STATUS));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if (validFilter) {
            Set<BigInteger> shiftIds = shiftDTOS.stream().map(shiftDTO -> shiftDTO.getId()).collect(Collectors.toSet());
            List<BigInteger> shiftStateIds = shiftStateService.findAllByShiftIdsByAccessgroupRole(shiftIds, filterCriteriaMap.get(TA_STATUS));
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if (shiftStateIds.contains(shiftDTO.getId())) {
                    filteredShifts.add((T) shiftDTO);
                }
            }
        }
            return filteredShifts;
        }

}
