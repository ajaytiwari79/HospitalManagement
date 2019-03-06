package com.kairos.dto.activity.wta;

import java.time.LocalDate;

public class IntervalBalance {

    private float total;
    private float scheduled;
    private float available;
    private LocalDate startDate;
    private LocalDate endDate;
    private float approved;

    public IntervalBalance() {
    }

    public IntervalBalance(float total, float scheduled, float available, LocalDate startDate, LocalDate endDate,float approved) {
        this.total = total;
        this.scheduled = scheduled;
        this.available = available;
        this.startDate = startDate;
        this.endDate = endDate;
        this.approved = approved;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getScheduled() {
        return scheduled;
    }

    public void setScheduled(float scheduled) {
        this.scheduled = scheduled;
    }

    public float getAvailable() {
        return available;
    }

    public void setAvailable(float available) {
        this.available = available;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public float getApproved() {
        return approved;
    }

    public void setApproved(float approved) {
        this.approved = approved;
    }
}
