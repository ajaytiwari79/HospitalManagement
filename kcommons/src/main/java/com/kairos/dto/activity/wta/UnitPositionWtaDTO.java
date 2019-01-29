package com.kairos.dto.activity.wta;


import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.enums.shift.PaidOutFrequencyEnum;

public class UnitPositionWtaDTO {
    private Long id;
    private Long expertiseId;
    private Long startDateMillis;
    private Long endDateMillis;
    private int totalWeeklyMinutes;
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyCost;
    private Double salary;
    private Long employmentTypeId;
    private Long unitId;
    private Long seniorityLevelId;
    private PaidOutFrequencyEnum paidOutFrequencyEnum;
    private WTAResponseDTO wtaResponseDTO;
    private Long staffId;

    public UnitPositionWtaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
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

    public float getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(float hourlyCost) {
        this.hourlyCost = hourlyCost;
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

    public WTAResponseDTO getWtaResponseDTO() {
        return wtaResponseDTO;
    }

    public void setWtaResponseDTO(WTAResponseDTO wtaResponseDTO) {
        this.wtaResponseDTO = wtaResponseDTO;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
