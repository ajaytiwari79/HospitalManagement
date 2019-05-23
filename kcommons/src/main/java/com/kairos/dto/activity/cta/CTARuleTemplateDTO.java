package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.enums.CalculationUnit;
import com.kairos.enums.cta.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.CollectionUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.FUNCTIONS;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTARuleTemplateDTO {

    private BigInteger id;
    @NotBlank(message = "error.cta.ruleTemplate.name.notEmpty")
    private String name;
    private String description;
    private boolean disabled;
    private String ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    @Valid
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    private List<CTARuleTemplatePhaseInfo> phaseInfo;
    private BudgetType budgetType;
    private List<CalculateValueIfPlanned> calculateValueIfPlanned;
    private List<Long> employmentTypes = new ArrayList<>();
    private PlanningCategory planningCategory;
    private List<Long> staffFunctions = new ArrayList<>();
    private PlannedTimeWithFactor plannedTimeWithFactor;
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;
    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private List<BigInteger> activityIds;

    private Set<BigInteger> timeTypeIds;
    private Set<BigInteger> plannedTimeIds;

    private List<Long> dayTypeIds;
    private List<DayOfWeek> days;
    private List<LocalDate> publicHolidays;
    @NotNull
    private BigInteger ruleTemplateCategoryId;
    private String ruleTemplateCategoryName;
    private UserInfo lastModifiedBy;

    public CTARuleTemplateDTO() {
    }

    public CTARuleTemplateDTO(String name,BigInteger id,List<CTARuleTemplatePhaseInfo> phaseInfo, List<Long> employmentTypes, List<BigInteger> activityIds, Set<BigInteger> timeTypeIds, Set<BigInteger> plannedTimeIds) {
        this.name = name;
        this.id = id;
        this.phaseInfo = phaseInfo;
        this.employmentTypes = employmentTypes;
        this.activityIds = activityIds;
        this.timeTypeIds = timeTypeIds;
        this.plannedTimeIds = plannedTimeIds;
    }

    public String getRuleTemplateCategoryName() {
        return ruleTemplateCategoryName;
    }

    public void setRuleTemplateCategoryName(String ruleTemplateCategoryName) {
        this.ruleTemplateCategoryName = ruleTemplateCategoryName;
    }

    public List<LocalDate> getPublicHolidays() {
        return publicHolidays;
    }

    public void setPublicHolidays(List<LocalDate> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    public List<DayOfWeek> getDays() {
        return days;
    }

    public void setDays(List<DayOfWeek> days) {
        this.days = days;
    }

    public boolean isCalculateScheduledHours() {
        return calculateScheduledHours;
    }

    public void setCalculateScheduledHours(boolean calculateScheduledHours) {
        this.calculateScheduledHours = calculateScheduledHours;
    }

    public BigInteger getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategoryId(BigInteger ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(String ruleTemplateType) {
        this.ruleTemplateType = ruleTemplateType;
    }

    public String getPayrollType() {
        return payrollType;
    }

    public void setPayrollType(String payrollType) {
        this.payrollType = payrollType;
    }

    public String getPayrollSystem() {
        return payrollSystem;
    }

    public void setPayrollSystem(String payrollSystem) {
        this.payrollSystem = payrollSystem;
    }

    public CalculationUnit getCalculationUnit() {
        return calculationUnit;
    }

    public void setCalculationUnit(CalculationUnit calculationUnit) {
        this.calculationUnit = calculationUnit;
    }

    public CompensationTable getCompensationTable() {
        return compensationTable;
    }

    public void setCompensationTable(CompensationTable compensationTable) {
        this.compensationTable = compensationTable;
    }

    public CalculateValueAgainst getCalculateValueAgainst() {
        return calculateValueAgainst;
    }

    public void setCalculateValueAgainst(CalculateValueAgainst calculateValueAgainst) {
        this.calculateValueAgainst = calculateValueAgainst;
    }

    public ApprovalWorkFlow getApprovalWorkFlow() {
        return approvalWorkFlow;
    }

    public void setApprovalWorkFlow(ApprovalWorkFlow approvalWorkFlow) {
        this.approvalWorkFlow = approvalWorkFlow;
    }

    public List<CTARuleTemplatePhaseInfo> getPhaseInfo() {
        return phaseInfo;
    }

    public void setPhaseInfo(List<CTARuleTemplatePhaseInfo> phaseInfo) {
        this.phaseInfo = Optional.ofNullable(phaseInfo).orElse(new ArrayList<>());
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public List<CalculateValueIfPlanned> getCalculateValueIfPlanned() {
        return calculateValueIfPlanned;
    }

    public void setCalculateValueIfPlanned(List<CalculateValueIfPlanned> calculateValueIfPlanned) {
        this.calculateValueIfPlanned = calculateValueIfPlanned;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }



    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = isNull(dayTypeIds) ? new ArrayList<>() : dayTypeIds;
    }

    public PlanningCategory getPlanningCategory() {
        return planningCategory;
    }

    public void setPlanningCategory(PlanningCategory planningCategory) {
        this.planningCategory = planningCategory;
    }

    public List<Long> getStaffFunctions() {
        return staffFunctions;
    }

    public void setStaffFunctions(List<Long> staffFunctions) {
        this.staffFunctions = staffFunctions;
    }

    public PlannedTimeWithFactor getPlannedTimeWithFactor() {
        return plannedTimeWithFactor;
    }

    public void setPlannedTimeWithFactor(PlannedTimeWithFactor plannedTimeWithFactor) {
        this.plannedTimeWithFactor = plannedTimeWithFactor;
    }


    public ActivityTypeForCostCalculation getActivityTypeForCostCalculation() {
        return activityTypeForCostCalculation;
    }

    public void setActivityTypeForCostCalculation(ActivityTypeForCostCalculation activityTypeForCostCalculation) {
        this.activityTypeForCostCalculation = activityTypeForCostCalculation;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = isNull(activityIds) ? new ArrayList<>() : activityIds;
    }

    public Set<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(Set<BigInteger> timeTypeIds) {
        this.timeTypeIds = isNull(timeTypeIds) ? new HashSet<>() : timeTypeIds;
    }

    public Set<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(Set<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = isNull(plannedTimeIds) ? new HashSet<>() : plannedTimeIds;
    }

    public CalculationFor getCalculationFor() {
        return calculationFor;
    }

    public void setCalculationFor(CalculationFor calculationFor) {
        this.calculationFor = calculationFor;
    }

    public UserInfo getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(UserInfo lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    private boolean isPhaseValid(BigInteger shiftPhaseId){
        return this.getPhaseInfo().stream().filter(p -> shiftPhaseId.equals(p.getPhaseId())).findFirst().isPresent();
    }

    private boolean isActivityAndTimeTypeAndPlannedTimeValid(BigInteger activityId,BigInteger timeTypeId,List<PlannedTime> plannedTimes){
        return (this.getActivityIds().contains(activityId) || this.getTimeTypeIds().contains(timeTypeId)) && CollectionUtils.containsAny(this.getPlannedTimeIds(),plannedTimes.stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
    }

    private boolean isEmployementTypeValid(Long employmentId){
        return this.getEmploymentTypes().contains(employmentId);
    }

    public boolean isRuleTemplateValid(Long employmentId,BigInteger shiftPhaseId,BigInteger activityId,BigInteger timeTypeId,List<PlannedTime> plannedTimes){
        return isPhaseValid(shiftPhaseId) && isEmployementTypeValid(employmentId) && (isActivityAndTimeTypeAndPlannedTimeValid(activityId,timeTypeId,plannedTimes) || this.getCalculationFor().equals(FUNCTIONS));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("description", description)
                .append("disabled", disabled)
                .append("ruleTemplateType", ruleTemplateType)
                .append("payrollType", payrollType)
                .append("payrollSystem", payrollSystem)
                .append("calculationUnit", calculationUnit)
                .append("compensationTable", compensationTable)
                .append("calculateValueAgainst", calculateValueAgainst)
                .append("approvalWorkFlow", approvalWorkFlow)
                .append("phaseInfo", phaseInfo)
                .append("budgetType", budgetType)
                .append("calculateValueIfPlanned", calculateValueIfPlanned)
                .append("employmentTypes", employmentTypes)
//                .append("activityType", activityType)
                .append("planningCategory", planningCategory)
                .append("staffFunctions", staffFunctions)
                .append("plannedTimeWithFactor", plannedTimeWithFactor)
//                .append("timeTypes", timeTypes)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTARuleTemplateDTO that = (CTARuleTemplateDTO) o;
        return disabled == that.disabled &&
                calculateScheduledHours == that.calculateScheduledHours &&
                Objects.equals(payrollType, that.payrollType) &&
                Objects.equals(payrollSystem, that.payrollSystem) &&
                calculationUnit == that.calculationUnit &&
                Objects.equals(compensationTable, that.compensationTable) &&
                Objects.equals(calculateValueAgainst, that.calculateValueAgainst) &&
                approvalWorkFlow == that.approvalWorkFlow &&
                Objects.equals(phaseInfo, that.phaseInfo) &&
                budgetType == that.budgetType &&
                Objects.equals(calculateValueIfPlanned, that.calculateValueIfPlanned) &&
                Objects.equals(employmentTypes, that.employmentTypes) &&
                planningCategory == that.planningCategory &&
                Objects.equals(staffFunctions, that.staffFunctions) &&
                Objects.equals(plannedTimeWithFactor, that.plannedTimeWithFactor) &&
                calculationFor == that.calculationFor &&
                activityTypeForCostCalculation == that.activityTypeForCostCalculation &&
                Objects.equals(activityIds, that.activityIds) &&
                Objects.equals(timeTypeIds, that.timeTypeIds) &&
                Objects.equals(plannedTimeIds, that.plannedTimeIds) &&
                Objects.equals(dayTypeIds, that.dayTypeIds) &&
                Objects.equals(days, that.days) &&
                Objects.equals(publicHolidays, that.publicHolidays);
    }

    @Override
    public int hashCode() {

        return Objects.hash(disabled, payrollType, payrollSystem, calculationUnit, compensationTable, calculateValueAgainst, approvalWorkFlow, phaseInfo, budgetType, calculateValueIfPlanned, employmentTypes, planningCategory, staffFunctions, plannedTimeWithFactor, calculateScheduledHours, calculationFor, activityTypeForCostCalculation, activityIds, timeTypeIds, plannedTimeIds, dayTypeIds, days, publicHolidays);
    }
}

