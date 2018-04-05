package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private float hourlyWages;
    private long id;
    private EmploymentTypeQueryResult employmentType;
    private Map<String, Object> employmentTypes;
    private List<Map<String, Object>> seniorityLevels;
    private Double salary;
    private int totalWeeklyMinutes;
    private PositionCode positionCode;
    private WorkingTimeAgreement workingTimeAgreement;
    private CostTimeAgreement costTimeAgreement;
    private Organization union;
    private Long lastWorkingDateMillis;
    private Long parentUnitId;
    private Long unitId;
    private Long regionCodeId;

    private UnitPositionSeniorityLevelResponse seniorityLevel;


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

    public EmploymentTypeQueryResult getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentTypeQueryResult employmentType) {
        this.employmentType = employmentType;
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

    public long getId() {
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

    public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
    }

    public CostTimeAgreement getCostTimeAgreement() {
        return costTimeAgreement;
    }

    public void setCostTimeAgreement(CostTimeAgreement costTimeAgreement) {
        this.costTimeAgreement = costTimeAgreement;
    }

    public int getTotalWeeklyMinutes() {
        this.totalWeeklyMinutes = this.totalWeeklyMinutes % 60;
        return totalWeeklyMinutes;

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

    public Long getRegionCodeId() {
        return regionCodeId;
    }

    public void setRegionCodeId(Long regionCodeId) {
        this.regionCodeId = regionCodeId;
    }

    public UnitPositionSeniorityLevelResponse getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(UnitPositionSeniorityLevelResponse seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public Map<String, Object> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(Map<String, Object> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Map<String, Object>> getSeniorityLevels() {
        return seniorityLevels;
    }

    public void setSeniorityLevels(List<Map<String, Object>> seniorityLevels) {
        this.seniorityLevels = seniorityLevels;
    }

    public UnitPositionQueryResult() {
        //default cons
    }


    public UnitPositionQueryResult(Expertise expertise, Long startDateMillis, int workingDaysInWeek, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, float hourlyWages, long id, EmploymentTypeQueryResult employmentType, Double salary, PositionCode positionCode, Organization union, Long lastWorkingDateMillis, CostTimeAgreement cta, WorkingTimeAgreement wta) {
        this.expertise = expertise;
        this.startDateMillis = startDateMillis;
        this.workingDaysInWeek = workingDaysInWeek;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.hourlyWages = hourlyWages;
        this.id = id;
        this.employmentType = employmentType;
        this.salary = salary;
        this.positionCode = positionCode;
        this.union = union;
        this.costTimeAgreement = cta;
        this.workingTimeAgreement = wta;
    }

}

