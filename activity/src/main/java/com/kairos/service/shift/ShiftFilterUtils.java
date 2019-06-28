package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
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
        List<T> shiftDTOS = new ArrayList<>(shiftWithActivityDTOS.size());
        if(isNull(staffFilterDTO)){
            staffFilterDTO = new StaffFilterDTO();
            staffFilterDTO.setFiltersData(new ArrayList<>());
        }
        Map<FilterType,Set<String>> filterTypeMap = staffFilterDTO.getFiltersData().stream().collect(Collectors.toMap(FilterSelectionDTO::getName, v->v.getValue()));
        for (T shiftWithActivityDTO : shiftWithActivityDTOS) {
            boolean validShift = isValidShiftActivityTimeCalculation(filterTypeMap, shiftWithActivityDTO) && isValidActivityTimetype(filterTypeMap, shiftWithActivityDTO);
            if(validShift){
                shiftDTOS.add(shiftWithActivityDTO);
            }
        }
        return shiftWithActivityDTOS;
    }

    private static <T extends ShiftDTO> boolean isValidActivityTimetype(Map<FilterType, Set<String>> filterTypeMap, T shiftWithActivityDTO) {
        boolean validShift = true;
        if(filterTypeMap.containsKey(TIME_TYPE)){
            Set<TimeTypeEnum> timeTypeEnums = new HashSet<>();
            shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
                timeTypeEnums.add(shiftActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType());
                shiftActivityDTO.getChildActivities().forEach(childActivityDTO ->  timeTypeEnums.add(childActivityDTO.getActivity().getBalanceSettingsActivityTab().getTimeType()));
            });
            if(!CollectionUtils.containsAny(filterTypeMap.get(TIME_TYPE),timeTypeEnums)){
                validShift = false;
            }
        }
        return validShift;
    }

    private static <T extends ShiftDTO> boolean isValidShiftActivityTimeCalculation(Map<FilterType, Set<String>> filterTypeMap, T shiftWithActivityDTO) {
        boolean validShift = true;
        if(filterTypeMap.containsKey(ACTIVITY_TIMECALCULATION_TYPE)){
            Set<String> methodForCalulation = new HashSet<>();
            shiftWithActivityDTO.getActivities().forEach(shiftActivityDTO -> {
                methodForCalulation.add(shiftActivityDTO.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime());
                shiftActivityDTO.getChildActivities().forEach(childActivityDTO ->  methodForCalulation.add(childActivityDTO.getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()));
            });
            if(!CollectionUtils.containsAny(filterTypeMap.get(ACTIVITY_TIMECALCULATION_TYPE),methodForCalulation)){
                validShift = false;
            }
        }
        return validShift;
    }
}
