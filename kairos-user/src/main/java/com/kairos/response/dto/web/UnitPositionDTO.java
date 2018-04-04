package com.kairos.response.dto.web;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pawanmandhan on 27/7/17.
 */
public class UnitPositionDTO {

    @NotNull(message = "Position code  is required for position")
    @Range(min = 0, message = "Position code is required for position")
    private Long positionCodeId;
    @NotNull(message = "expertise is required for position")
    @Range(min = 0, message = "expertise is required for position")
    private Long expertiseId;

    private Long startDateMillis;
    private Long endDateMillis;
    private Long lastWorkingDateMillis;

    @Range(min = 0, max = 60, message = "Incorrect Weekly minute")
    private int totalWeeklyMinutes;
    @Range(min = 0, message = "Incorrect Weekly Hours")
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    private Double salary;
    private Long employmentTypeId;
    private String employmentType;
    @NotNull(message = "wta can't be null")
    private Long wtaId;
    private Long ctaId;
    @NotNull(message = "staffId is missing")
    @Range(min = 0, message = "staffId is missing")
    private Long staffId;
    // private Long expiryDate;

    private Long unionId;
    private Long parentUnitId;

    @NotNull(message = "unitId  is required for position")
    @Range(min = 0, message = "unit Id  is required for position")
    private Long unitId;

    @NotNull(message = "reasonCodeId  is required for position")
    @Range(min = 0, message = "reasonCode Id  is required for position")
    private Long reasonCodeId;

    @NotNull(message = "seniorityLevel  is required for position")
    @Range(min = 0, message = "seniorityLevel  is required for position")
    private Long seniorityLevelId;

    private Set<Long> functionIds = new HashSet<>();


    private Long timeCareExternalId;

    public UnitPositionDTO() {
        //default cons
    }


    public UnitPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes,
                           float avgDailyWorkingHours, float hourlyWages, Double salary, Long employmentTypeId) {
        this.salary = salary;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.hourlyWages = hourlyWages;
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.employmentTypeId = employmentTypeId;

    }


    public UnitPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyHours, Long employmentTypeId,
                           Long staffId, Long wtaId, Long ctaId, Long unitId, Long timeCareExternalId) {
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
        this.wtaId = wtaId;
        this.ctaId = ctaId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyHours = totalWeeklyHours;
        this.timeCareExternalId = timeCareExternalId;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.hourlyWages = hourlyWages;
        this.salary = salary;
        this.unitId = unitId;
        this.workingDaysInWeek = workingDaysInWeek;
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

    public void setEmploymentTypeId(Long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Long getPositionCodeId() {
        return positionCodeId;
    }

    public void setPositionCodeId(Long positionCodeId) {
        this.positionCodeId = positionCodeId;
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

    public Integer getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(Integer totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
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

    public Long getWtaId() {
        return wtaId;
    }

    public void setWtaId(Long wtaId) {
        this.wtaId = wtaId;
    }

    public Long getCtaId() {
        return ctaId;
    }

    public void setCtaId(Long ctaId) {
        this.ctaId = ctaId;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }


    public long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnionId() {
        return unionId;
    }

    public void setUnionId(Long unionId) {
        this.unionId = unionId;
    }

    public Long getLastWorkingDateMillis() {
        return lastWorkingDateMillis;
    }

    public void setLastWorkingDateMillis(Long lastWorkingDateMillis) {
        this.lastWorkingDateMillis = lastWorkingDateMillis;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getTimeCareExternalId() {
        return timeCareExternalId;
    }

    public void setTimeCareExternalId(Long timeCareExternalId) {
        this.timeCareExternalId = timeCareExternalId;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public Long getSeniorityLevelId() {
        return seniorityLevelId;
    }

    public void setSeniorityLevelId(Long seniorityLevelId) {
        this.seniorityLevelId = seniorityLevelId;
    }


    public Set<Long> getFunctionIds() {
        return functionIds;
    }

    public void setFunctionIds(Set<Long> functionIds) {
        this.functionIds = functionIds;
    }
}
