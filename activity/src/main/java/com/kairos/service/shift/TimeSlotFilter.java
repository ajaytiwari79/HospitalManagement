package com.kairos.service.shift;

import com.kairos.commons.utils.TimeInterval;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.FilterType;

import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.DateUtils.asZoneDateTime;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.TIME_SLOT;

/**
 * Created By G.P.Ranjan on 3/7/19
 **/
public class TimeSlotFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;
    private List<TimeSlotDTO> timeSlotDTOS;

    public TimeSlotFilter(Map<FilterType, Set<String>> filterCriteriaMap,List<TimeSlotDTO> timeSlotDTOS) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.timeSlotDTOS = timeSlotDTOS;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TIME_SLOT) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_SLOT));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        List<TimeInterval> timeIntervals = new ArrayList<>();
        if(validFilter){
            filterCriteriaMap.get(TIME_SLOT).forEach(timeSlot->{
                for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                    if(timeSlot.equals(timeSlotDTO.getName()) || timeSlot.equals(timeSlotDTO.getId().intValue())){
                        timeIntervals.add(new TimeInterval((timeSlotDTO.getStartHour()* AppConstants.ONE_HOUR_MINUTES)+timeSlotDTO.getStartMinute(),(timeSlotDTO.getEndHour()*AppConstants.ONE_HOUR_MINUTES)+timeSlotDTO.getEndMinute()-1));
                    }
                }
            });
            for (ShiftDTO shiftDTO : shiftDTOS) {
                for (TimeInterval timeInterval : timeIntervals) {
                    if (timeInterval.contains(asZoneDateTime(shiftDTO.getStartDate()).get(ChronoField.MINUTE_OF_DAY))) {
                        filteredShifts.add((T) shiftDTO);
                        break;
                    }
                }
            }
        }
        return filteredShifts;
    }
}
