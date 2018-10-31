package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.dto.activity.shift.Expertise;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.organization.OrganizationDTO;
import com.kairos.dto.user.organization.position_code.PositionCodeDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author pradeep
 * @date - 8/8/18
 */

public class UnitPositionDTO {

    private Expertise expertise;
    @JsonIgnore
    private boolean deleted;
    private LocalDate startDate;
    private int workingDaysInWeek;
    private LocalDate endDate;
    private Long lastModificationDate;
    private int totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int fullTimeWeeklyMinutes;
    private float hourlyCost;
    private Long id;

    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;
    private BigInteger workingTimeAgreementId;
    private Double salary;
    private int totalWeeklyMinutes;
    private PositionCodeDTO positionCode;
    private CTAResponseDTO costTimeAgreement;
    private BigInteger costTimeAgreementId;
    private OrganizationDTO union;
    private Long lastWorkingDateMillis;
    private Long parentUnitId;
    private Long unitId;
    private Long reasonCodeId;
    private Map<String, Object> unitInfo;
    private WTAResponseDTO workingTimeAgreement;


    private Boolean history;
    private Boolean editable;
    private boolean published;

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


    public BigInteger getCostTimeAgreementId() {
        return costTimeAgreementId;
    }

    public void setCostTimeAgreementId(BigInteger costTimeAgreementId) {
        this.costTimeAgreementId = costTimeAgreementId;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
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

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public float getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(float hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Map<String, Object> employmentType) {
        this.employmentType = employmentType;
    }

    public Map<String, Object> getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(Map<String, Object> seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public BigInteger getWorkingTimeAgreementId() {
        return workingTimeAgreementId;
    }

    public void setWorkingTimeAgreementId(BigInteger workingTimeAgreementId) {
        this.workingTimeAgreementId = workingTimeAgreementId;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public PositionCodeDTO getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCodeDTO positionCode) {
        this.positionCode = positionCode;
    }

    public CTAResponseDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CTAResponseDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public OrganizationDTO getUnion() {
        return union;
    }

    public void setUnion(OrganizationDTO union) {
        this.union = union;
    }

    public Long getLastWorkingDateMillis() {
        return lastWorkingDateMillis;
    }

    public void setLastWorkingDateMillis(Long lastWorkingDateMillis) {
        this.lastWorkingDateMillis = lastWorkingDateMillis;
    }

    public Long getParentUnitId() {
        return parentUnitId;
    }

    public void setParentUnitId(Long parentUnitId) {
        this.parentUnitId = parentUnitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getReasonCodeId() {
        return reasonCodeId;
    }

    public void setReasonCodeId(Long reasonCodeId) {
        this.reasonCodeId = reasonCodeId;
    }

    public Map<String, Object> getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(Map<String, Object> unitInfo) {
        this.unitInfo = unitInfo;
    }

    public WTAResponseDTO getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WTAResponseDTO workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
