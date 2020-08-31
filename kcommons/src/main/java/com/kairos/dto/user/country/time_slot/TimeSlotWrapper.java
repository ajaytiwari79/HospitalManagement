package com.kairos.dto.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prabjot on 18/12/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeSlotWrapper {

    private Long id;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private boolean shiftStartTime;
    private String name;

    public ZonedDateTime getStartZoneDateTime(LocalDate localDate) {
        return DateUtils.asZonedDateTime(localDate, LocalTime.of(startHour,startMinute));
    }

    public ZonedDateTime getEndZoneDateTime(LocalDate localDate) {
        if(endHour<startHour){
            localDate = localDate.plusDays(1);
        }
        return DateUtils.asZonedDateTime(localDate, LocalTime.of(endHour,endMinute));
    }

    public DateTimeInterval getTimeSlotInterval(LocalDate startLocalDate) {
        ZonedDateTime startZonedDateTime = this.getStartZoneDateTime(startLocalDate);
        ZonedDateTime endZonedDateTime = this.getEndZoneDateTime(startLocalDate);
        List<DateTimeInterval> timeIntervals = new ArrayList<>();
        return new DateTimeInterval(startZonedDateTime,endZonedDateTime);
    }
}
