package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.TIME_TYPE;

/**
 * Created by pradeep
 * Created at 30/6/19
 **/

public class TimeTypeFilter implements ShiftFilter{

    Map<FilterType, Set<String>> filterCriteriaMap;

    public TimeTypeFilter(Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_TYPE));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                Set<String> timeTypeEnums = new HashSet<>();
                shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                    timeTypeEnums.add(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType().toString());
                    shiftActivityDTO.getChildActivities().forEach(childActivityDTO ->  timeTypeEnums.add(childActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType().toString()));
                });
                if(CollectionUtils.containsAny(filterCriteriaMap.get(TIME_TYPE),timeTypeEnums) || isNotNull(shiftDTO.getRequestAbsence())){
                    filteredShifts.add((T)shiftDTO);
                }
            }

        }
        return filteredShifts;
    }
}
