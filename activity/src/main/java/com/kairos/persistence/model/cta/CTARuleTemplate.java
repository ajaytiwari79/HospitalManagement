package com.kairos.persistence.model.cta;


import com.kairos.dto.activity.cta.*;
import com.kairos.enums.cta.*;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;

import java.math.BigInteger;
import java.util.*;

/**
 * @author pradeep
 * @date - 30/7/18
 */

public class CTARuleTemplate extends MongoBaseEntity {

    protected String name;
    protected String description;
    protected boolean disabled;
    protected BigInteger ruleTemplateCategoryId;
    private String ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    private List<CTARuleTemplatePhaseInfo> phaseInfo = new ArrayList<>(); //Query beforeStart
    private BudgetType budgetType;

    private List<CalculateValueIfPlanned> calculateValueIfPlanned = new ArrayList<>();
    private List<Long> employmentTypes = new ArrayList<>();
    private PlanningCategory planningCategory;
    private List<Long> staffFunctions = new ArrayList<>();
    private PlannedTimeWithFactor plannedTimeWithFactor;
    private Long createdBy;
    private Long lastModifiedBy;

    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private List<Long> activityIds;
    private Set<Long> timeTypeIds;
    private Set<Long> plannedTimeIds;
    private List<Long> dayTypeIds;
    //it describe that this template is scheduledHoursTemplate or not
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;
    private Long countryId;


    public CTARuleTemplate() {

    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public BigInteger getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategoryId(BigInteger ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public CTARuleTemplate(String name, String description, String payrollType, String payrollSystem, BigInteger ruleTemplateCategoryId,CalculationUnit calculationUnit,CompensationTable compensationTable,CalculateValueAgainst calculateValueAgainst,ApprovalWorkFlow approvalWorkFlow,BudgetType budgetType,ActivityTypeForCostCalculation activityTypeForCostCalculation,PlanningCategory planningCategory,PlannedTimeWithFactor plannedTimeWithFactor,Long countryId) {
        this.name = name;
        this.description = description;
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
        this.payrollType = payrollType;
        this.payrollSystem = payrollSystem;
        this.calculationUnit = calculationUnit;
        this.compensationTable = compensationTable;
        this.calculateValueAgainst = calculateValueAgainst;
        this.approvalWorkFlow = approvalWorkFlow;
        this.budgetType = budgetType;
        this.activityTypeForCostCalculation = activityTypeForCostCalculation;
        this.planningCategory = planningCategory;
        this.plannedTimeWithFactor = plannedTimeWithFactor;
        this.countryId = countryId;


    }


    public boolean isCalculateScheduledHours() {
        return calculateScheduledHours;
    }

    public void setCalculateScheduledHours(boolean calculateScheduledHours) {
        this.calculateScheduledHours = calculateScheduledHours;
    }

    public CalculationFor getCalculationFor() {
        return calculationFor;
    }

    public void setCalculationFor(CalculationFor calculationFor) {
        this.calculationFor = calculationFor;
    }

    public CTARuleTemplate buildCTARuleTemplateFromDTO(CTARuleTemplateDTO ctaRuleTemplateDTO) {
        return this;
    }

    public ActivityTypeForCostCalculation getActivityTypeForCostCalculation() {
        return activityTypeForCostCalculation;
    }

    public void setActivityTypeForCostCalculation(ActivityTypeForCostCalculation activityTypeForCostCalculation) {
        this.activityTypeForCostCalculation = activityTypeForCostCalculation;
    }

    public List<Long> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Long> activityIds) {
        this.activityIds = activityIds;
    }

    public Set<Long> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(Set<Long> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public Set<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(Set<Long> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
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
        this.phaseInfo = phaseInfo;
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





   /* public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }*/

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

    /*public CTARuleTemplateType getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(CTARuleTemplateType ruleTemplateType) {
        this.ruleTemplateType = ruleTemplateType;
    }*/

    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
    }

    public String getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(String ruleTemplateType) {
        this.ruleTemplateType = ruleTemplateType;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        if (this.getId() != null)
            throw new UnsupportedOperationException("can't modified this property");
        this.createdBy = createdBy;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }



    public static void setActivityBasesCostCalculationSettings(CTARuleTemplate ctaRuleTemplate) {

        switch (ctaRuleTemplate.getActivityTypeForCostCalculation()) {
            case TIME_TYPE_ACTIVITY:
                ctaRuleTemplate.setActivityIds(new ArrayList<>());
                break;
            default:
                ctaRuleTemplate.setPlannedTimeIds(null);
                ctaRuleTemplate.setTimeTypeIds(null);
                break;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CTARuleTemplate that = (CTARuleTemplate) o;
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
                activityTypeForCostCalculation == that.activityTypeForCostCalculation &&
                Objects.equals(activityIds, that.activityIds) &&
                Objects.equals(timeTypeIds, that.timeTypeIds) &&
                Objects.equals(plannedTimeIds, that.plannedTimeIds) &&
                Objects.equals(dayTypeIds, that.dayTypeIds) &&
                calculationFor == that.calculationFor;
    }

    @Override
    public int hashCode() {

        return Objects.hash(disabled, payrollType, payrollSystem, calculationUnit, compensationTable, calculateValueAgainst, approvalWorkFlow, phaseInfo, budgetType, calculateValueIfPlanned, employmentTypes, planningCategory, staffFunctions, plannedTimeWithFactor, activityTypeForCostCalculation, activityIds, timeTypeIds, plannedTimeIds, dayTypeIds, calculateScheduledHours, calculationFor);
    }
}

