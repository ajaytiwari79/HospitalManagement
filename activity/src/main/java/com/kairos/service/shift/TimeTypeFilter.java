package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.TIME_TYPE;

/**
 * Created by pradeep
 * Created at 30/6/19
 **/

public class TimeTypeFilter implements ShiftFilter{
    List<BigInteger> selectedTimeTypes;
    Map<FilterType, Set<String>> filterCriteriaMap;

    public TimeTypeFilter(Map<FilterType, Set<String>> filterCriteriaMap, List<BigInteger> selectedTimeTypes) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.selectedTimeTypes = selectedTimeTypes;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_TYPE));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                List<BigInteger> timeTypeIds = new ArrayList<>();
                shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                    timeTypeIds.add(shiftActivityDTO.getActivity().getTimeType().getId());
                    shiftActivityDTO.getChildActivities().forEach(childActivityDTO ->  timeTypeIds.add(childActivityDTO.getActivity().getTimeType().getId()));
                });
                if(CollectionUtils.containsAny(selectedTimeTypes,timeTypeIds)){
                    filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
