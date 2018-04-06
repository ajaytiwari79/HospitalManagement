package com.kairos.activity.response.dto.counter;

import java.time.DayOfWeek;
import java.util.List;

public class CounterFilterDTO {
    List<DayOfWeek> days;

    public List<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(List<DayOfWeek> days) {
        this.days = days;
    }
}
