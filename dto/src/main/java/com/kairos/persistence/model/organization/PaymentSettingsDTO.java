package com.kairos.persistence.model.organization;

import org.hibernate.validator.constraints.Range;

import java.time.DayOfWeek;

/**
 * Created by vipul on 12/4/18.
 */
public class PaymentSettingsDTO {
    private Long id;
    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    private Long lastFornigthtlyPayDate;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDate;

    public PaymentSettingsDTO() {
        // default cons
    }

    public PaymentSettingsDTO(Long id, DayOfWeek weeklyPayDay, DayOfWeek fornightlyPayDay, Long monthlyPayDate) {
        this.id = id;
        this.weeklyPayDay = weeklyPayDay;
        this.fornightlyPayDay = fornightlyPayDay;
        this.monthlyPayDate = monthlyPayDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonthlyPayDate() {
        return monthlyPayDate;
    }

    public void setMonthlyPayDate(Long monthlyPayDate) {
        this.monthlyPayDate = monthlyPayDate;
    }

    public DayOfWeek getWeeklyPayDay() {
        return weeklyPayDay;
    }

    public void setWeeklyPayDay(DayOfWeek weeklyPayDay) {
        this.weeklyPayDay = weeklyPayDay;
    }

    public DayOfWeek getFornightlyPayDay() {
        return fornightlyPayDay;
    }

    public void setFornightlyPayDay(DayOfWeek fornightlyPayDay) {
        this.fornightlyPayDay = fornightlyPayDay;
    }

    public Long getLastFornigthtlyPayDate() {
        return lastFornigthtlyPayDate;
    }

    public void setLastFornigthtlyPayDate(Long lastFornigthtlyPayDate) {
        this.lastFornigthtlyPayDate = lastFornigthtlyPayDate;
    }
}
