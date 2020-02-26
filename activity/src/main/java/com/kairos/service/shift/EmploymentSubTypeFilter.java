package com.kairos.service.shift;

import com.kairos.constants.ApiConstants;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.utils.counter.KPIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.EMPLOYMENT_SUB_TYPE;
import static com.kairos.enums.FilterType.EMPLOYMENT_TYPE;

public class EmploymentSubTypeFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeMap;

    public EmploymentSubTypeFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long, EmploymentSubType> employmentIdAndEmploymentSubTypeMap) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.employmentIdAndEmploymentSubTypeMap = employmentIdAndEmploymentSubTypeMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = (filterCriteriaMap.containsKey(EMPLOYMENT_SUB_TYPE) && isCollectionNotEmpty(filterCriteriaMap.get(EMPLOYMENT_SUB_TYPE)));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(filterCriteriaMap.get(EMPLOYMENT_SUB_TYPE).contains(employmentIdAndEmploymentSubTypeMap.get(shiftDTO.getEmploymentId()).name()))
                    filteredShifts.add((T)shiftDTO);
            }
        }
        return filteredShifts;
    }
}
