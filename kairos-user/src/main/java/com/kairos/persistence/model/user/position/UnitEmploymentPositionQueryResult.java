package com.kairos.persistence.model.user.position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 10/8/17.
 */

@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitEmploymentPositionQueryResult {
    private Expertise expertise;
    @JsonIgnore
    private boolean deleted;
    private Long startDateMillis;
    private int workingDaysInWeek;
    private Long endDateMillis;
    private Long lastModificationDate;
    private int totalWeeklyHours;
    private float avgDailyWorkingHours;
    private float hourlyWages;
    private long id;
    private EmploymentType employmentType;
    private float salary;
    private int totalWeeklyMinutes;
    private PositionCode positionCode;
    private WorkingTimeAgreement workingTimeAgreement;
    private CostTimeAgreement costTimeAgreement;

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }


    public float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
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

    public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public CostTimeAgreement getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CostTimeAgreement costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public int getTotalWeeklyMinutes() {
        this.totalWeeklyMinutes = this.totalWeeklyMinutes % 60;
        return totalWeeklyMinutes;

    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {

        this.totalWeeklyMinutes = totalWeeklyMinutes;

    }

    public int getTotalWeeklyHours() {
        this.totalWeeklyHours = this.totalWeeklyMinutes / 60;
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public UnitEmploymentPositionQueryResult() {
        //default cons
    }

    public UnitEmploymentPositionQueryResult(Expertise expertise, Long startDateMillis, int workingDaysInWeek, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, float hourlyWages, long id, EmploymentType employmentType, float salary, PositionCode positionCode, WorkingTimeAgreement workingTimeAgreement, CostTimeAgreement costTimeAgreement) {
        this.expertise = expertise;
        this.startDateMillis = startDateMillis;
        this.workingDaysInWeek = workingDaysInWeek;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.hourlyWages = hourlyWages;
        this.id = id;
        this.employmentType = employmentType;
        this.salary = salary;
        this.positionCode = positionCode;
        this.workingTimeAgreement = workingTimeAgreement;
        this.costTimeAgreement = costTimeAgreement;
    }

    public UnitEmploymentPositionQueryResult(Expertise expertise, boolean deleted, Long startDateMillis, int workingDaysInWeek, Long endDateMillis, Long lastModificationDate, int totalWeeklyHours, float avgDailyWorkingHours, float hourlyWages, long id, EmploymentType employmentType, float salary, int totalWeeklyMinutes, PositionCode positionCode, WorkingTimeAgreement workingTimeAgreement, CostTimeAgreement costTimeAgreement) {
        this.expertise = expertise;
        this.deleted = deleted;
        this.startDateMillis = startDateMillis;
        this.workingDaysInWeek = workingDaysInWeek;
        this.endDateMillis = endDateMillis;
        this.lastModificationDate = lastModificationDate;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.hourlyWages = hourlyWages;
        this.id = id;
        this.employmentType = employmentType;
        this.salary = salary;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.positionCode = positionCode;
        this.workingTimeAgreement = workingTimeAgreement;
        this.costTimeAgreement = costTimeAgreement;
    }
}
