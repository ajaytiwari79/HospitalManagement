package com.kairos.persistence.model.user.staff;

import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.UserBaseEntity;

/**
 * Created by prabjot on 6/12/16.
 */
@NodeEntity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Wage extends UserBaseEntity {

    private long startDate;
    private long endDate;
    private float salary;
    private int hourlyWage;
    private int fixedHourlyWage;
    private boolean hasHourlyWage;

    public Wage(long startDate, long endDate, float salary, int hourlyWage) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.salary = salary;
        this.hourlyWage = hourlyWage;
    }

    public Wage(){}

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public void setHourlyWage(int hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public float getSalary() {
        return salary;
    }

    public int getHourlyWage() {
        return hourlyWage;
    }

    public void setFixedHourlyWage(int fixedHourlyWage) {
        this.fixedHourlyWage = fixedHourlyWage;
    }

    public int getFixedHourlyWage() {
        return fixedHourlyWage;
    }

    public void setHasHourlyWage(boolean hasHourlyWage) {
        this.hasHourlyWage = hasHourlyWage;
    }

    public boolean isHasHourlyWage() {
        return hasHourlyWage;
    }
}
