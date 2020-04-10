package com.kairos.commons.utils.filter_utils;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.FilterType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterUtils {


    public static <T> Map<FilterType, Set<T>> filterOutEmptyQueriesAndPrepareMap(ShiftSearchDTO shiftSearchDTO) {
        return shiftSearchDTO.getFiltersData().stream().filter(e -> e.getValue().size() > 0).collect(Collectors.toMap(FilterSelectionDTO::getName, FilterSelectionDTO::getValue));
    }


}
