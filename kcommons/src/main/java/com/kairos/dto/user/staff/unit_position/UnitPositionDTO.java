package com.kairos.dto.user.staff.unit_position;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.experties.ExpertiseResponseDTO;
import com.kairos.dto.user.country.experties.FunctionsDTO;
import com.kairos.enums.employment_type.EmploymentCategory;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pawanmandhan on 27/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnitPositionDTO {

    @NotNull(message = "expertise is required for position")
    @Range(min = 0, message = "expertise is required for position")
    private Long expertiseId;
    private Long id;
    private Long positionLineId;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastWorkingDate;

    @Range(min = 0, max = 60, message = "Incorrect Weekly minute")
    private int totalWeeklyMinutes;
    @Range(min = 0, message = "Incorrect Weekly Hours")
    private int totalWeeklyHours;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private BigDecimal hourlyCost;
    private Double salary;
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
    private Set<FunctionsDTO> functions = new HashSet<>();
    private Long timeCareExternalId;
    private boolean published;
    private Long accessGroupId;
    private boolean mainUnitPosition;
    private float taxDeductionPercentage;
    private ExpertiseResponseDTO expertise;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;





    public UnitPositionDTO() {
        //default cons
    }


    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public UnitPositionDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, int totalWeeklyMinutes,
                           float avgDailyWorkingHours, BigDecimal hourlyCost, Double salary, Long employmentTypeId) {
        this.salary = salary;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.hourlyCost = hourlyCost;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentTypeId = employmentTypeId;

    }


    public UnitPositionDTO(Long expertiseId, LocalDate startDate, LocalDate endDate, int totalWeeklyHours, Long employmentTypeId,
                           Long staffId, BigInteger wtaId, BigInteger ctaId, Long unitId, Long timeCareExternalId) {
        this.expertiseId = expertiseId;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
        this.wtaId = wtaId;
        this.ctaId = ctaId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
        this.timeCareExternalId = timeCareExternalId;
        this.unitId = unitId;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getAccessGroupId() {
        return accessGroupId;
    }

    public void setAccessGroupId(Long accessGroupId) {
        this.accessGroupId = accessGroupId;
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

    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
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


    public Long getEmploymentTypeId() {
        return employmentTypeId;
    }


    /*public void setEmploymentTypeId(long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }*/

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

    public LocalDate getLastWorkingDate() {
        return lastWorkingDate;
    }

    public void setLastWorkingDate(LocalDate lastWorkingDate) {
        this.lastWorkingDate = lastWorkingDate;
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


    public Set<FunctionsDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(Set<FunctionsDTO> functions) {
        this.functions = functions;
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

    public Long getPositionLineId() {
        return positionLineId;
    }

    public void setPositionLineId(Long positionLineId) {
        this.positionLineId = positionLineId;
    }

    public boolean isMainUnitPosition() {
        return mainUnitPosition;
    }

    public void setMainUnitPosition(boolean mainUnitPosition) {
        this.mainUnitPosition = mainUnitPosition;
    }

    public float getTaxDeductionPercentage() {
        return taxDeductionPercentage;
    }

    public void setTaxDeductionPercentage(float taxDeductionPercentage) {
        this.taxDeductionPercentage = taxDeductionPercentage;
    }

    public ExpertiseResponseDTO getExpertise() {
        return expertise;
    }

    public void setExpertise(ExpertiseResponseDTO expertise) {
        this.expertise = expertise;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }
}
