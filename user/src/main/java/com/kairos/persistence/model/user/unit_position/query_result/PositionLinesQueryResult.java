package com.kairos.persistence.model.user.unit_position.query_result;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

/**
 * CreatedBy vipulpandey on 26/9/18
 **/
@QueryResult
public class PositionLinesQueryResult {
    private Long id;
    private Long unitPositionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDaysInWeek;
    private Integer totalWeeklyHours;
    private Float avgDailyWorkingHours;
    private Integer fullTimeWeeklyMinutes;
    private Double salary;
    private Integer totalWeeklyMinutes;
    private Float hourlyWages;
    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;

    public PositionLinesQueryResult() {
        //DC
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(Integer workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(Integer totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public Float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(Float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public Integer getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(Integer fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(Float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public Map<String, Object> getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Map<String, Object> employmentType) {
        this.employmentType = employmentType;
    }

    public Map<String, Object> getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(Map<String, Object> seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(Integer totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }
}
