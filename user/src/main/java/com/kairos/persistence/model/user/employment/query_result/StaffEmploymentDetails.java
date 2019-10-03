package com.kairos.persistence.model.user.employment.query_result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by vipul on 29/1/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffEmploymentDetails {
    private Expertise expertise;
    @JsonIgnore
    private boolean deleted;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long lastModificationDate;
    private long id;
    private WTAResponseDTO workingTimeAgreement;
    private CTAResponseDTO costTimeAgreement;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private Long staffId;
    private Long userId;
    private Long countryId;
    // TODO MOVING THIS INSIDE SO THAT WE CAN REMOVE THE UPPER LEVEL WRAPPER
    private List<DayOfWeek> activityDayTypes;
    private ZoneId unitTimeZone;
    private Staff staff;
    private List<EmploymentLinesQueryResult> employmentLines;
    private List<FunctionDTO> appliedFunctions;
    private EmploymentSubType employmentSubType;

    public EmploymentSubType getEmploymentSubType() {
        return employmentSubType;
    }

    public void setEmploymentSubType(EmploymentSubType employmentSubType) {
        this.employmentSubType = employmentSubType;
    }

    public StaffEmploymentDetails() {
        //Default Constructor
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

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

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
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


    public List<FunctionDTO> getAppliedFunctions() {
        return appliedFunctions;
    }

    public void setAppliedFunctions(List<FunctionDTO> appliedFunctions) {
        this.appliedFunctions = appliedFunctions;
    }

    public List<EmploymentLinesQueryResult> getEmploymentLines() {
        return employmentLines;
    }

    public void setEmploymentLines(List<EmploymentLinesQueryResult> employmentLines) {
        this.employmentLines = employmentLines;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
