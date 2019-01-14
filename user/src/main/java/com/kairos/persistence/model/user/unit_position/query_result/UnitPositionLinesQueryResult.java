package com.kairos.persistence.model.user.unit_position.query_result;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Map;

/**
 * CreatedBy vipulpandey on 26/9/18
 **/
@QueryResult
public class UnitPositionLinesQueryResult {
    private Long id;
    private Long unitPositionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDaysInWeek;
    private Integer totalWeeklyHours;
    private Float avgDailyWorkingHours;
    private Integer fullTimeWeeklyMinutes;
    private Double salary;
    private Integer totalWeeklyMinutes;
    private BigDecimal hourlyCost;
    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;
    private WTAResponseDTO workingTimeAgreement;
    private CTAResponseDTO costTimeAgreement;

    public UnitPositionLinesQueryResult() {
        //DC
    }

    public UnitPositionLinesQueryResult(Long id, LocalDate startDate, LocalDate endDate, Integer workingDaysInWeek, Integer totalWeeklyHours, Float avgDailyWorkingHours, Integer fullTimeWeeklyMinutes, Double salary, Integer totalWeeklyMinutes, BigDecimal hourlyCost, Map<String, Object> employmentType, Map<String, Object> seniorityLevel,Long unitPositionId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.salary = salary;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.hourlyCost = hourlyCost;
        this.employmentType = employmentType;
        this.seniorityLevel = seniorityLevel;
        this.unitPositionId=unitPositionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(Integer workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(Integer totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public Float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(Float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public Integer getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(Integer fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
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

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Integer getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(Integer totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
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
}
