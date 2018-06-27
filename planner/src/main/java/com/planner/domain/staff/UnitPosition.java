package com.planner.domain.staff;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.planner.domain.MongoBaseEntity;
import com.planner.domain.wta.templates.WorkingTimeAgreement;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class UnitPosition  extends MongoBaseEntity {

    private Long expertiseId;
    private Long positionCodeId;
    private Long startDateMillis;
    private Long endDateMillis;
    private int totalWeeklyMinutes;
    private int totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    private Double salary;
    private Long employmentTypeId;
    private Long unitId;
    private Long seniorityLevelId;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private WorkingTimeAgreement workingTimeAgreement;
    private String staffId;

    public UnitPosition() {
    }

    public UnitPosition(Long expertiseId, Long positionCodeId, Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes, int totalWeeklyHours, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, Double salary, Long employmentTypeId, Long unitId, Long seniorityLevelId, PaidOutFrequencyEnum paidOutFrequencyEnum, WorkingTimeAgreement workingTimeAgreement, BigInteger kairosId,String staffId) {
        this.expertiseId = expertiseId;
        this.positionCodeId = positionCodeId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.workingDaysInWeek = workingDaysInWeek;
        this.hourlyWages = hourlyWages;
        this.salary = salary;
        this.employmentTypeId = employmentTypeId;
        this.unitId = unitId;
        this.seniorityLevelId = seniorityLevelId;
        this.paidOutFrequencyEnum = paidOutFrequencyEnum;
        this.workingTimeAgreement = workingTimeAgreement;
        this.kairosId=kairosId;
        this.staffId=staffId;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public Long getPositionCodeId() {
        return positionCodeId;
    }

    public void setPositionCodeId(Long positionCodeId) {
        this.positionCodeId = positionCodeId;
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

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }

    public PaidOutFrequencyEnum getPaidOutFrequencyEnum() {
        return paidOutFrequencyEnum;
    }

    public void setPaidOutFrequencyEnum(PaidOutFrequencyEnum paidOutFrequencyEnum) {
        this.paidOutFrequencyEnum = paidOutFrequencyEnum;
    }


    public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
