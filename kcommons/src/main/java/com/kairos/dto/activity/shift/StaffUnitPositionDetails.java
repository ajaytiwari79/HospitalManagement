package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.employment.UnitPositionLinesDTO;
import com.kairos.dto.user.staff.staff.Staff;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Created by vipul on 29/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffUnitPositionDetails {
    private Expertise expertise;
    private Staff staff;
    private EmploymentType employmentType;
    private WTAResponseDTO workingTimeAgreement;
    private int workingDaysInWeek;
    private LocalDate endDate;
    private LocalDate StartDate;
    private Long lastModificationDate;
    private Integer totalWeeklyHours;
    private Integer fullTimeWeeklyMinutes;
    private Float avgDailyWorkingHours;
    private Long id;
    private Long staffId;
    private Long userId;
    private Float salary;
    private int totalWeeklyMinutes;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private ZoneId unitTimeZone;
    private Long countryId;
    private List<FunctionDTO> appliedFunctions= new ArrayList<>();
    private BigInteger excludedPlannedTime;
    private BigInteger includedPlannedTime;
    private Long unitId;
    private BigDecimal hourlyCost;
    private Long functionId;
    private List<UnitPositionLinesDTO> positionLines;
    private Boolean history;
    private Boolean editable;
    private boolean published;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;
    private CTAResponseDTO costTimeAgreement;

    public StaffUnitPositionDetails() {

    }

    public StaffUnitPositionDetails(Long unitId) {
        this.unitId = unitId;
    }

    public StaffUnitPositionDetails(int workingDaysInWeek, int totalWeeklyMinutes) {
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }


    public void setCtaRuleTemplates(List<CTARuleTemplateDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = Optional.ofNullable(ctaRuleTemplates).orElse(new ArrayList<>());
    }
    public List<FunctionDTO> getAppliedFunctions() {
        return Optional.ofNullable(appliedFunctions).orElse(new ArrayList<>());
    }

    public void setAppliedFunctions(List<FunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return StartDate;
    }

    public void setStartDate(LocalDate startDate) {
        StartDate = startDate;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Integer getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(Integer totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public Integer getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(Integer fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(Float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Float getSalary() {
        return salary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getExcludedPlannedTime() {
        return excludedPlannedTime;
    }

    public void setExcludedPlannedTime(BigInteger excludedPlannedTime) {
        this.excludedPlannedTime = excludedPlannedTime;
    }

    public BigInteger getIncludedPlannedTime() {
        return includedPlannedTime;
    }

    public void setIncludedPlannedTime(BigInteger includedPlannedTime) {
        this.includedPlannedTime = includedPlannedTime;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UnitPositionLinesDTO> getPositionLines() {
        return positionLines;
    }

    public void setPositionLines(List<UnitPositionLinesDTO> positionLines) {
        this.positionLines = positionLines;
    }

    public CTAResponseDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CTAResponseDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public Boolean getHistory() {
        return history;
    }

    public void setHistory(Boolean history) {
        this.history = history;
    }

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }

    public LocalDate getAccumulatedTimebankDate() {
        return accumulatedTimebankDate;
    }

    public void setAccumulatedTimebankDate(LocalDate accumulatedTimebankDate) {
        this.accumulatedTimebankDate = accumulatedTimebankDate;
    }
}