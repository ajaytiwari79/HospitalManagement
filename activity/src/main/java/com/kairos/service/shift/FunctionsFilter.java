package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.FUNCTIONS;

/**
 * Created By G.P.Ranjan on 19/9/19
 **/
public class FunctionsFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Set<LocalDate> functionDates;
    public FunctionsFilter(Map<FilterType, Set<G>> filterCriteriaMap, Set<LocalDate> functionDates) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.functionDates = functionDates;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(FUNCTIONS);
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(functionDates.contains(asLocalDate(shiftDTO.getStartDate()))) {
                    filteredShifts.add((T) shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
