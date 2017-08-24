package com.kairos.persistence.model.user.position;

import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 10/8/17.
 */
@QueryResult
public class PositionQueryResult {
    private Expertise expertise;
    private boolean isEnabled = true;
    private Long startDate;

    private Long endDate;
    private Long lastModificationDate;
    private int totalWeeklyHours;
    private float avgMonthlyWorkingHours;
    private float hourlyWages;
    public enum EmploymentType{
        FULL_TIME,PART_TIME
    };
    private long id;
    private Position.EmploymentType employmentType;
    private float salary;

    private PositionName positionName;

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public float getAvgMonthlyWorkingHours() {
        return avgMonthlyWorkingHours;
    }

    public void setAvgMonthlyWorkingHours(float avgMonthlyWorkingHours) {
        this.avgMonthlyWorkingHours = avgMonthlyWorkingHours;
    }

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public Position.EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Position.EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public PositionName getPositionName() {
        return positionName;
    }

    public void setPositionName(PositionName positionName) {
        this.positionName = positionName;
    }

    public long getId() {
        return id;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setId(long id) {
        this.id = id;
    }
}
