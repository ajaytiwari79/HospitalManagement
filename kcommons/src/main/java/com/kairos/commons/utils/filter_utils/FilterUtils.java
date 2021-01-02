package com.kairos.commons.utils.filter_utils;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.FilterType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

public class FilterUtils {

    private FilterUtils() {

    }

    public static <T> Object[] filterOutEmptyQueriesAndPrepareMap(ShiftSearchDTO shiftSearchDTO) {
        Map<FilterType,Set<T>> filterTypeSetMap = new HashMap<>();
        boolean existsShiftFilter = false;
        for (FilterSelectionDTO filtersDatum : shiftSearchDTO.getFiltersData()) {
            if(isCollectionNotEmpty(filtersDatum.getValue())){
                filterTypeSetMap.put(filtersDatum.getName(),filtersDatum.getValue());
            }
            if(!existsShiftFilter && FilterType.MatchType.SHIFT.equals(filtersDatum.getName().getMatchType())){
                existsShiftFilter = true;
            }
        }
        return new Object[]{filterTypeSetMap,existsShiftFilter};
    }


}
