package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypeEnum;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.enums.FilterType.ACTIVITY_TIMECALCULATION_TYPE;
import static com.kairos.enums.FilterType.TIME_TYPE;

/**
 * Created by pradeep
 * Created at 27/6/19
 **/

public class ShiftFilterUtils {

    public static <T extends ShiftDTO> List<T> getShiftsByFilters(List<T> shiftWithActivityDTOS, StaffFilterDTO staffFilterDTO){
        if(isNull(staffFilterDTO)){
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
        Map<FilterType,Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v->v.getValue()));
        ShiftFilter timeTypeFilter = new TimeTypeFilter(filterTypeMap);
        ShiftFilter activityTimecalculationTypeFilter = new ActivityTimeCalculationTypeFilter(filterTypeMap);
        ShiftFilter activityStatusFilter = new ActivityStatusFilter(filterTypeMap);
        ShiftFilter shiftFilter = new AndShiftFilter(timeTypeFilter,activityTimecalculationTypeFilter).and(activityStatusFilter);
        return shiftFilter.meetCriteria(shiftWithActivityDTOS);
    }
}
