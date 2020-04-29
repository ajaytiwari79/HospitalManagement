package com.kairos.commons.utils.filter_utils;

import com.kairos.dto.activity.shift.ShiftSearchDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.FilterType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

public class FilterUtils {

    private FilterUtils() {

    }

    public static <T> Map<FilterType, Set<T>> filterOutEmptyQueriesAndPrepareMap(ShiftSearchDTO shiftSearchDTO) {
        return shiftSearchDTO.getFiltersData().stream().filter(e -> isCollectionNotEmpty(e.getValue())).collect(Collectors.toMap(FilterSelectionDTO::getName, FilterSelectionDTO::getValue));
    }


}
