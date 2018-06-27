package com.kairos.persistence.model.organization;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.hibernate.validator.constraints.Range;
import org.neo4j.ogm.annotation.NodeEntity;

import java.time.DayOfWeek;

/**
 * Created by vipul on 12/4/18.
 */
@NodeEntity
public class PaymentSettings extends UserBaseEntity {

    private DayOfWeek weeklyPayDay;
    private DayOfWeek fornightlyPayDay;
    private Long lastFornightlyPayDate;
    @Range(min = 1l, max = 31L)
    private Long monthlyPayDate;


    public PaymentSettings() {
        // default cons
    }

    public PaymentSettings(DayOfWeek weeklyPayDay, DayOfWeek fornightlyPayDay, Long monthlyPayDate, Long lastFornightlyPayDate) {
        this.weeklyPayDay = weeklyPayDay;
        this.monthlyPayDate = monthlyPayDate;
        this.fornightlyPayDay = fornightlyPayDay;
        this.lastFornightlyPayDate = lastFornightlyPayDate;
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

    public Long getLastFornightlyPayDate() {
        return lastFornightlyPayDate;
    }

    public void setLastFornightlyPayDate(Long lastFornightlyPayDate) {
        this.lastFornightlyPayDate = lastFornightlyPayDate;
    }

    public Long getMonthlyPayDate() {
        return monthlyPayDate;
    }

    public void setMonthlyPayDate(Long monthlyPayDate) {
        this.monthlyPayDate = monthlyPayDate;
    }
}
