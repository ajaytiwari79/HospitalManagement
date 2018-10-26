package com.kairos.dto.activity.period;

import java.time.LocalDate;

public class FlippingDateDTO {
    private LocalDate date;
    private int hours;
    private int minutes;
    private int seconds;


    public FlippingDateDTO() {
    }

    public FlippingDateDTO(LocalDate date, int hours, int minutes,int seconds) {
        this.date = date;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public LocalDate getDate() {

        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
