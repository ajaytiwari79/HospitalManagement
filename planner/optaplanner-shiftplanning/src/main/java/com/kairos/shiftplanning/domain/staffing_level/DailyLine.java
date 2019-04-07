package com.kairos.shiftplanning.domain.staffing_level;


import org.joda.time.LocalDate;

public abstract class DailyLine {
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    protected LocalDate date;
}
