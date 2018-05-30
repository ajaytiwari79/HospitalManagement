package com.kairos.activity.spec.night_shift;

import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.spec.AbstractActivitySpecification;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.persistence.model.common.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ValidNightShiftSpecification extends AbstractActivitySpecification<ShiftQueryResult> {

    private TimeSlot timeSlot;
    private Integer minMinutesToCheckNightShift;


    private Long addHoursAndMinutes(Long millis, int minutes, int hours){
        return millis + (hours * 60 * 60 * 60) + (minutes * 60 * 60);
    }


    @Override
    public boolean isSatisfied(ShiftQueryResult shiftQueryResult) {

        DateTimeInterval shiftDateTimeInterval = new DateTimeInterval(shiftQueryResult.getStartDate().longValue(), shiftQueryResult.getEndDate().longValue());

        LocalDate startLocalDate = DateUtils.getLocalDate(shiftQueryResult.getStartDate());
        LocalDate endLocalDate = DateUtils.getLocalDate(shiftQueryResult.getEndDate());

        DateTimeInterval dateTimeInterval = new DateTimeInterval(
                addHoursAndMinutes(DateUtils.convertLocalDateToDate(startLocalDate).getTime(), timeSlot.getStartHour(), timeSlot.getStartMinute()),
                addHoursAndMinutes(DateUtils.convertLocalDateToDate(endLocalDate).getTime(), timeSlot.getEndHour(), timeSlot.getEndMinute()));

        Long overlappedMillis = 0L;

        // Case 1
//        LocalDateTime startDate = LocalDateTime.of(DateUtils.getLocalDateFromDate(shiftQueryResult.getStartDate()),
//                LocalTime.of(timeSlot.getStartHour(), timeSlot.getStartMinute()));
//
//        LocalDateTime endDate = LocalDateTime.of(DateUtils.getLocalDateFromDate(shiftQueryResult.getEndDate()),
//                LocalTime.of(timeSlot.getEndHour(), timeSlot.getEndMinute()));


        return true;
    }
}
