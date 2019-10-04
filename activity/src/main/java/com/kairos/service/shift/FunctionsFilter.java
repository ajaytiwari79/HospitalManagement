package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.FUNCTIONS;

/**
 * Created By G.P.Ranjan on 19/9/19
 **/
public class FunctionsFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;
    private List<Date> functionDates;
    public FunctionsFilter(Map<FilterType, Set<String>> filterCriteriaMap, List<Date> functionDates) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.functionDates = functionDates;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(FUNCTIONS) && isCollectionNotEmpty(functionDates);
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(functionDates.contains(shiftDTO.getStartDate()))
                    filteredShifts.add((T)shiftDTO);
            }
        }
        return filteredShifts;
    }
}
