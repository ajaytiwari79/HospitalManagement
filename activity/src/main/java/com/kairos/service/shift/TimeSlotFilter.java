package com.kairos.service.shift;

import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.enums.FilterType;
import com.kairos.rest_client.UserIntegrationService;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

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
            for (String timeSlotName : filterCriteriaMap.get(TIME_SLOT)) {
                for (TimeSlotDTO timeSlotDTO : timeSlotDTOS) {
                    if(timeSlotName.equals(timeSlotDTO.getName())){
                        timeIntervals.add(new TimeInterval((timeSlotDTO.getStartHour()*60)+timeSlotDTO.getStartMinute(),(timeSlotDTO.getEndHour()*60)+timeSlotDTO.getEndMinute()-1));
                    }
                }
            }
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
