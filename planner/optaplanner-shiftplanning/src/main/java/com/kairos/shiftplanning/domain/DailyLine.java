package com.kairos.shiftplanning.domain;


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
