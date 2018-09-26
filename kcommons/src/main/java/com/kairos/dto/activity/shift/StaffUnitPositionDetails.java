package com.kairos.dto.activity.shift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.country.experties.AppliedFunctionDTO;
import com.kairos.dto.user.staff.staff.Staff;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * Created by vipul on 29/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffUnitPositionDetails {
    private Expertise expertise;
    private Staff staff;
    private EmploymentType employmentType;
    private WTAResponseDTO workingTimeAgreement;
    private Long startDateMillis;
    private int workingDaysInWeek;
    private Long endDateMillis;
    private Long lastModificationDate;
    private int totalWeeklyHours;
    private int fullTimeWeeklyMinutes;
    private float avgDailyWorkingHours;
    private float hourlyWages;
    private long id;
    private Long staffId;
    private float salary;
    private int totalWeeklyMinutes;
    private BigInteger workingTimeAgreementId;
    private BigInteger costTimeAgreementId;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private ZoneId unitTimeZone;
    private Long countryId;
    private List<AppliedFunctionDTO> appliedFunctions;
    private LocalDate unitPositionStartDate;
    private LocalDate unitPositionEndDate;
    private BigInteger excludedPlannedTime;
    private BigInteger includedPlannedTime;
    private Date startDate;
    private Date endDate;
    private Long unitId;

    public StaffUnitPositionDetails() {

    }

    public StaffUnitPositionDetails(Long unitId) {
        this.unitId = unitId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public LocalDate getUnitPositionStartDate() {
        return unitPositionStartDate;
    }

    public void setUnitPositionStartDate(LocalDate unitPositionStartDate) {
        this.unitPositionStartDate = unitPositionStartDate;
    }

    public LocalDate getUnitPositionEndDate() {
        return unitPositionEndDate;
    }

    public void setUnitPositionEndDate(LocalDate unitPositionEndDate) {
        this.unitPositionEndDate = unitPositionEndDate;
    }


    public BigInteger getCostTimeAgreementId() {
        return costTimeAgreementId;
    }

    public void setCostTimeAgreementId(BigInteger costTimeAgreementId) {
        this.costTimeAgreementId = costTimeAgreementId;
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

    public List<CTARuleTemplateDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }

    public BigInteger getWorkingTimeAgreementId() {
        return workingTimeAgreementId;
    }

    public void setWorkingTimeAgreementId(BigInteger workingTimeAgreementId) {
        this.workingTimeAgreementId = workingTimeAgreementId;
    }

    public StaffUnitPositionDetails(int workingDaysInWeek, int totalWeeklyMinutes) {
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }


    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
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

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
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

    public List<AppliedFunctionDTO> getAppliedFunctions() {
        return appliedFunctions;
    }

    public void setAppliedFunctions(List<AppliedFunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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
}