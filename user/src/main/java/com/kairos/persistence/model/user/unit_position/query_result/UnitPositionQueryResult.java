package com.kairos.persistence.model.user.unit_position.query_result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * Created by vipul on 10/8/17.
 */

@QueryResult
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionQueryResult {
    private Expertise expertise;
    @JsonIgnore
    private boolean deleted;
    private Long startDateMillis;
    private int workingDaysInWeek;
    private Long endDateMillis;
    private Long lastModificationDate;
    private int totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int fullTimeWeeklyMinutes;
    private float hourlyWages;
    private Long id;

    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;
    private BigInteger workingTimeAgreementId;
    private Double salary;
    private int totalWeeklyMinutes;
    private PositionCode positionCode;
    private CTAResponseDTO costTimeAgreement;
    private Organization union;
    private Long lastWorkingDateMillis;
    private Long parentUnitId;
    private Long unitId;
    private Long reasonCodeId;
    private Map<String, Object> unitInfo;
    private WTAResponseDTO workingTimeAgreement;
    private BigInteger costTimeAgreementId;


    private Boolean history;
    private Boolean editable;
    private Boolean published;
    private BigDecimal hourlyCost;

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


    public BigInteger getCostTimeAgreementId() {
        return costTimeAgreementId;
    }

    public void setCostTimeAgreementId(BigInteger costTimeAgreementId) {
        this.costTimeAgreementId = costTimeAgreementId;
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

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
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

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
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


    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public Long getId() {
        return id;
    }

    public Long getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Long lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    /*public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }
*/
    public CTAResponseDTO getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CTAResponseDTO costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public int getTotalWeeklyMinutes() {
        return this.totalWeeklyMinutes;

    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {

        this.totalWeeklyMinutes = totalWeeklyMinutes;

    }

    public int getTotalWeeklyHours() {
        this.totalWeeklyHours = this.totalWeeklyMinutes / 60;
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
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

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public UnitPositionQueryResult() {
        //default cons
    }

    public UnitPositionQueryResult(Expertise expertise, Long startDateMillis, int workingDaysInWeek, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, float hourlyWages, long id, Double salary, PositionCode positionCode, Organization union, Long lastWorkingDateMillis, CTAResponseDTO cta, WTAResponseDTO wta) {
        this.expertise = expertise;
        this.startDateMillis = startDateMillis;
        this.workingDaysInWeek = workingDaysInWeek;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.hourlyWages = hourlyWages;
        this.lastWorkingDateMillis = lastWorkingDateMillis;
        this.id = id;
        this.salary = salary;
        this.positionCode = positionCode;
        this.union = union;
        this.costTimeAgreement = cta;
        this.workingTimeAgreement = wta;
    }

}
