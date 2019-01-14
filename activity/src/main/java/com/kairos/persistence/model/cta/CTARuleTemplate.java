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



    public boolean isCalculatedValueChanged(CTARuleTemplate ctaRuleTemplate){
        boolean isCalculatedValueChanged = false;
        if (ctaRuleTemplate != null) {
            isCalculatedValueChanged = !(disabled == ctaRuleTemplate.disabled &&
                    calculateScheduledHours == ctaRuleTemplate.calculateScheduledHours &&
                    Objects.equals(payrollType, ctaRuleTemplate.payrollType) &&
                    Objects.equals(payrollSystem, ctaRuleTemplate.payrollSystem) &&
                    calculationUnit == ctaRuleTemplate.calculationUnit &&
                    Objects.equals(compensationTable, ctaRuleTemplate.compensationTable) &&
                    Objects.equals(calculateValueAgainst, ctaRuleTemplate.calculateValueAgainst) &&
                    approvalWorkFlow == ctaRuleTemplate.approvalWorkFlow &&
                    Objects.equals(phaseInfo, ctaRuleTemplate.phaseInfo) &&
                    budgetType == ctaRuleTemplate.budgetType &&
                    Objects.equals(calculateValueIfPlanned, ctaRuleTemplate.calculateValueIfPlanned) &&
                    Objects.equals(employmentTypes, ctaRuleTemplate.employmentTypes) &&
                    planningCategory == ctaRuleTemplate.planningCategory &&
                    Objects.equals(staffFunctions, ctaRuleTemplate.staffFunctions) &&
                    Objects.equals(plannedTimeWithFactor, ctaRuleTemplate.plannedTimeWithFactor) &&
                    activityTypeForCostCalculation == ctaRuleTemplate.activityTypeForCostCalculation &&
                    Objects.equals(activityIds, ctaRuleTemplate.activityIds) &&
                    Objects.equals(timeTypeIds, ctaRuleTemplate.timeTypeIds) &&
                    Objects.equals(plannedTimeIds, ctaRuleTemplate.plannedTimeIds) &&
                    Objects.equals(dayTypeIds, ctaRuleTemplate.dayTypeIds) &&
                    calculationFor == ctaRuleTemplate.calculationFor);
        }
        return isCalculatedValueChanged;
    }
}

