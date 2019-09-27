package com.kairos.service.shift;

import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.RealTimeStatus;
import com.kairos.enums.shift.ShiftType;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.REAL_TIME_STATUS;

/**
 * Created By G.P.Ranjan on 23/9/19
 **/
public class RealTimeStatusFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;
    private Set<BigInteger> selectedSickTimeTypeIds;

    public RealTimeStatusFilter(Map<FilterType, Set<String>> filterCriteriaMap, Set<BigInteger> selectedSickTimeTypeIds) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.selectedSickTimeTypeIds = selectedSickTimeTypeIds;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterCriteriaMap.get(REAL_TIME_STATUS));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            Date currentDate = getDate();
            for (ShiftDTO shiftDTO : shiftDTOS) {
                List<BigInteger> currentOnBreakActivityIds = new ArrayList<>();
                boolean shiftOnBreak = false;
                if(isCollectionNotEmpty(shiftDTO.getBreakActivities())) {
                    for (ShiftActivityDTO shiftActivityDTO : shiftDTO.getBreakActivities()) {
                        if (shiftActivityDTO.getStartDate().before(currentDate) && shiftActivityDTO.getEndDate().after(currentDate)) {
                            currentOnBreakActivityIds.add(shiftActivityDTO.getActivityId());
                        }
                    }
                    shiftOnBreak = shiftDTO.getBreakActivities().stream().anyMatch(shiftActivityDTO -> currentOnBreakActivityIds.contains(shiftActivityDTO.getActivityId()));
                }
                Set<BigInteger> timeTypeIds = new HashSet<>();
                shiftDTO.getActivities().forEach(shiftActivityDTO -> {
                    timeTypeIds.add(shiftActivityDTO.getActivity().getTimeType().getId());
                    shiftActivityDTO.getChildActivities().forEach(childActivityDTO ->  timeTypeIds.add(childActivityDTO.getActivity().getTimeType().getId()));
                });
                if (isCurrentDayShift(shiftDTO) && ((filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.ON_BREAK.toString()) && shiftOnBreak) ||
                        (filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.SICK.toString()) && CollectionUtils.containsAny(selectedSickTimeTypeIds,timeTypeIds))  ||
                        (filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.UPCOMING.toString()) && shiftDTO.getStartDate().after(currentDate))  ||
                        (filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.RESTING.toString()) && !(ShiftType.ABSENCE.equals(shiftDTO.getShiftType()) && (CommonConstants.FULL_WEEK.equals(shiftDTO.getActivities().get(0).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(shiftDTO.getActivities().get(0).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()))) && (shiftDTO.getStartDate().after(currentDate) || shiftDTO.getEndDate().before(currentDate)))  ||
                        (filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.ON_LEAVE.toString()) && ShiftType.ABSENCE.equals(shiftDTO.getShiftType()) && (CommonConstants.FULL_WEEK.equals(shiftDTO.getActivities().get(0).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime()) || CommonConstants.FULL_DAY_CALCULATION.equals(shiftDTO.getActivities().get(0).getActivity().getTimeCalculationActivityTab().getMethodForCalculatingTime())))  ||
                        (filterCriteriaMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.CURRENTLY_WORKING.toString()) && shiftDTO.getStartDate().before(currentDate) && shiftDTO.getEndDate().after(currentDate)))) {
                   filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }

    private boolean isCurrentDayShift(ShiftDTO shiftDTO){
        Date currentDate = asDate(asZoneDateTime(getDate()).truncatedTo(ChronoUnit.DAYS));
        Date shiftDate = asDate(asZoneDateTime(shiftDTO.getStartDate()).truncatedTo(ChronoUnit.DAYS));
        return currentDate.equals(shiftDate);
    }
}
