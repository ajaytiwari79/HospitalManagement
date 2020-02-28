package com.kairos.service.shift;

import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.utils.counter.KPIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.*;

public class EmploymentTypeFilter <G> implements ShiftFilter {

    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Map<Long,Long> employmentIdAndEmploymentTypeIdMap;

    public EmploymentTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long,Long> selectedEmploymentTypeIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.employmentIdAndEmploymentTypeIdMap = selectedEmploymentTypeIds;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = (filterCriteriaMap.containsKey(EMPLOYMENT_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(EMPLOYMENT_TYPE)));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(KPIUtils.getLongValueSetBySetOfObjects(filterCriteriaMap.get(EMPLOYMENT_TYPE)).contains(employmentIdAndEmploymentTypeIdMap.getOrDefault(shiftDTO.getEmploymentId(), ApiConstants.DEFAULT_ID)))
                    filteredShifts.add((T)shiftDTO);
            }
        }
        return filteredShifts;
    }

}
