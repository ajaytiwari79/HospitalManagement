package com.kairos.user.staff.unit_position;

import com.kairos.enums.EmploymentCategory;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
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

    private LocalDate startLocalDate;
    private LocalDate endLocalDate;
    private LocalDate lastWorkingLocalDate;

    @Range(min = 0, max = 60, message = "Incorrect Weekly minute")
    private int totalWeeklyMinutes;
    @Range(min = 0, message = "Incorrect Weekly Hours")
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    private Double salary;
    @NotNull(message = "employmentTypeId can't be null")
    private Long employmentTypeId;
    @NotNull(message = "employmentTypeCategory can't be null")
    private EmploymentCategory employmentTypeCategory;
    @NotNull(message = "wta can't be null")
    private BigInteger wtaId;
    @NotNull(message = "cta can't be null")
    private BigInteger ctaId;
    @NotNull(message = "staffId is missing")
    @Range(min = 0, message = "staffId is missing")
    private Long staffId;
    // private Long expiryDate;

    private Long unionId;
    private Long parentUnitId;

    @NotNull(message = "unitId  is required for position")
    @Range(min = 0, message = "unit Id  is required for position")
    private Long unitId;

    private Long reasonCodeId;

    @NotNull(message = "seniorityLevel  is required for position")
    @Range(min = 0, message = "seniorityLevel  is required for position")
    private Long seniorityLevelId;
    private Set<Long> functionIds = new HashSet<>();
    private Long timeCareExternalId;
    private boolean published;

    public Long getAccessGroupIdOnEmploymentEnd() {
        return accessGroupIdOnEmploymentEnd;
    }

    public void setAccessGroupIdOnEmploymentEnd(Long accessGroupIdOnEmploymentEnd) {
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
    }

    private Long accessGroupIdOnEmploymentEnd;
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
      //  this.startDateMillis = startDateMillis;
       // this.endDateMillis = endDateMillis;
        this.employmentTypeId = employmentTypeId;

    }


    public UnitPositionDTO(Long positionCodeId, Long expertiseId, Long startDateMillis, Long endDateMillis, int totalWeeklyHours, Long employmentTypeId,
                           Long staffId, BigInteger wtaId, BigInteger ctaId, Long unitId, Long timeCareExternalId) {
        this.positionCodeId = positionCodeId;
        this.expertiseId = expertiseId;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
        this.wtaId = wtaId;
        this.ctaId = ctaId;
        //this.startDateMillis = startDateMillis;
        //this.endDateMillis = endDateMillis;
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

    public BigInteger getWtaId() {
        return wtaId;
    }

    public void setWtaId(BigInteger wtaId) {
        this.wtaId = wtaId;
    }

    public BigInteger getCtaId() {
        return ctaId;
    }

    public void setCtaId(BigInteger ctaId) {
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

    public LocalDate getLastWorkingLocalDate() {
        return lastWorkingLocalDate;
    }

    public void setLastWorkingLocalDate(LocalDate lastWorkingLocalDate) {
        this.lastWorkingLocalDate = lastWorkingLocalDate;
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

    public EmploymentCategory getEmploymentTypeCategory() {
        return employmentTypeCategory;
    }

    public void setEmploymentTypeCategory(EmploymentCategory employmentTypeCategory) {
        this.employmentTypeCategory = employmentTypeCategory;
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

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public void setStartLocalDate(LocalDate startLocalDate) {
        this.startLocalDate = startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public void setEndLocalDate(LocalDate endLocalDate) {
        this.endLocalDate = endLocalDate;
    }

}
