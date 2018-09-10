package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.dto.user.country.experties.AppliedFunctionDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by vipul on 29/1/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffUnitPositionDetails {
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
    private int fullTimeWeeklyMinutes;
    private PositionCode positionCode;
    private BigInteger workingTimeAgreementId;
    private BigInteger costTimeAgreementId;
    private WTAResponseDTO workingTimeAgreement;
    private CTAResponseDTO costTimeAgreement;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private Long staffId;
    private Long countryId;


    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    // TODO MOVING THIS INSIDE SO THAT WE CAN REMOVE THE UPPER LEVEL WRAPPER
    private List<DayOfWeek> activityDayTypes;
    private ZoneId unitTimeZone;
    private Staff staff;
    private List<AppliedFunctionDTO> appliedFunctions;

    public StaffUnitPositionDetails() {
    }


    public BigInteger getCostTimeAgreementId() {
        return costTimeAgreementId;
    }

    public void setCostTimeAgreementId(BigInteger costTimeAgreementId) {
        this.costTimeAgreementId = costTimeAgreementId;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public BigInteger getWorkingTimeAgreementId() {
        return workingTimeAgreementId;
    }

    public void setWorkingTimeAgreementId(BigInteger workingTimeAgreementId) {
        this.workingTimeAgreementId = workingTimeAgreementId;
    }

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
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

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public CTAResponseDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CTAResponseDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }


    public List<DayOfWeek> getActivityDayTypes() {
        return activityDayTypes;
    }

    public void setActivityDayTypes(List<DayOfWeek> activityDayTypes) {
        this.activityDayTypes = activityDayTypes;
    }


    public List<AppliedFunctionDTO> getAppliedFunctions() {
        return appliedFunctions;
    }

    public void setAppliedFunctions(List<AppliedFunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }
}
