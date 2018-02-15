package com.kairos.response.dto.web;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Created by pawanmandhan on 27/7/17.
 */
public class UnitEmploymentPositionDTO {

    @NotNull(message = "Position code  is required for position_code")
    @Range(min = 0, message = "Position code is required for position_code")
    private Long positionCodeId;
    @NotNull(message = "expertise is required for position_code")
    @Range(min = 0, message = "expertise is required for position_code")
    private Long expertiseId;

    private Long startDateMillis;
    private Long endDateMillis;

    @Range(min = 0, max = 60, message = "Incorrect Weekly minute")
    private int totalWeeklyMinutes;
    @Range(min = 0, message = "Incorrect Weekly Hours")
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    private float salary;
    private Long employmentTypeId;
    @NotNull(message = "wta can't be null")
    private Long wtaId;
    private Long ctaId;

//    private Position.EmploymentType employmentType;

    @NotNull(message = "staffId is missing")
    @Range(min = 0, message = "staffId is missing")
    private Long staffId;
    // private Long expiryDate;

    private Long unionId;

    public UnitEmploymentPositionDTO() {
        //default cons
    }


    public UnitEmploymentPositionDTO(Long positionCodeId, Long expertiseId) {
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
    }


    public UnitEmploymentPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes,
                                     float avgDailyWorkingHours, float hourlyWages, float salary, Long employmentTypeId) {
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

    public UnitEmploymentPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, float salary, Long employmentTypeId, Long staffId) {
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.workingDaysInWeek = workingDaysInWeek;
        this.hourlyWages = hourlyWages;
        this.salary = salary;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
    }

    public UnitEmploymentPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyHours, Long employmentTypeId, Long staffId, Long wtaId, Long ctaId) {
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
        this.wtaId = wtaId;
        this.ctaId = ctaId;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = 60;
        this.hourlyWages = 10;
        this.salary = 500;

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

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
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
}
