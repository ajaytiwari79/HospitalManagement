package com.kairos.response.dto.web.cta;

import com.kairos.persistence.model.user.agreement.cta.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateDTO {
    @NotNull
    public Long id;
    public boolean deleted;
    public String name;
    public String description;
    public boolean disabled;
    @NotNull
    public Long ruleTemplateCategoryId;
    private CTARuleTemplateType ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    private CTARuleTemplateDayTypeDTO ctaRuleTemplateDayTypeDTO;
    private PhaseInfo phaseInfo;
    private BudgetType budgetType;
    private List<Long> calculateValueIfPlannedIds =new ArrayList<>();
    private List<Long> employmentTypeIds =new ArrayList<>();
    private ActivityType activityType;
    private PlanningCategory planningCategory;
    private StaffFunction staffFunction;
    private PlannedTimeWithFactor plannedTimeWithFactor;
    private List<Long> timeTypeIds =new ArrayList<>();

    public CTARuleTemplateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public Long getRuleTemplateCategoryId() {
        return ruleTemplateCategoryId;
    }

    public void setRuleTemplateCategory(Long ruleTemplateCategoryId) {
        this.ruleTemplateCategoryId = ruleTemplateCategoryId;
    }

    public CTARuleTemplateType getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(CTARuleTemplateType ruleTemplateType) {
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

    public CTARuleTemplateDayTypeDTO getCtaRuleTemplateDayTypeDTO() {
        return ctaRuleTemplateDayTypeDTO;
    }

    public void setCtaRuleTemplateDayTypeDTO(CTARuleTemplateDayTypeDTO ctaRuleTemplateDayTypeDTO) {
        this.ctaRuleTemplateDayTypeDTO = ctaRuleTemplateDayTypeDTO;
    }

    public PhaseInfo getPhaseInfo() {
        return phaseInfo;
    }

    public void setPhaseInfo(PhaseInfo phaseInfo) {
        this.phaseInfo = phaseInfo;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public List<Long> getCalculateValueIfPlannedIds() {
        return calculateValueIfPlannedIds;
    }

    public void setCalculateValueIfPlannedIds(List<Long> calculateValueIfPlannedIds) {
        this.calculateValueIfPlannedIds = calculateValueIfPlannedIds;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public PlanningCategory getPlanningCategory() {
        return planningCategory;
    }

    public void setPlanningCategory(PlanningCategory planningCategory) {
        this.planningCategory = planningCategory;
    }

    public StaffFunction getStaffFunction() {
        return staffFunction;
    }

    public void setStaffFunction(StaffFunction staffFunction) {
        this.staffFunction = staffFunction;
    }

    public PlannedTimeWithFactor getPlannedTimeWithFactor() {
        return plannedTimeWithFactor;
    }

    public void setPlannedTimeWithFactor(PlannedTimeWithFactor plannedTimeWithFactor) {
        this.plannedTimeWithFactor = plannedTimeWithFactor;
    }

    public List<Long> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<Long> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }
}
