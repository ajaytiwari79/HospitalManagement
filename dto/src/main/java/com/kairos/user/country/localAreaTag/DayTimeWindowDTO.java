package com.kairos.user.country.localAreaTag;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 11/6/18
 */

public class DayTimeWindowDTO {

    private DayOfWeek dayOfWeek;
    private LocalTime fromTime;
    private LocalTime toTime;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalTime toTime) {
        this.toTime = toTime;
    }
}
