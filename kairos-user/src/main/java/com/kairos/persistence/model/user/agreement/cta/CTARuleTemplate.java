package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.agreement.wta.templates.RuleTemplateCategory;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.country.EmploymentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;
@NodeEntity
public class CTARuleTemplate extends RuleTemplate{
    private CTARuleTemplateType ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    private List<Long>dayTypes=new ArrayList<>();
    private PhaseInfo phaseInfo;
    private BudgetType budgetType;
    private List<AccessGroup> calculateValueIfPlanned=new ArrayList<>();
    private List<EmploymentType> employmentTypes=new ArrayList<>();
    private ActivityType activityType;
    private PlanningCategory planningCategory;
    private Function function;
    private PlannedTimeWithFactor plannedTimeWithFactor;
    private List<Long>timeTypes=new ArrayList<>();
    private User createdBy;
    private User lastModifiedBy;

    public CTARuleTemplate() {

    }

    public CTARuleTemplate(String name,String description,RuleTemplateCategory ruleTemplateCategory,
      CTARuleTemplateType ruleTemplateType,String payrollType,String payrollSystem) {
        this.name=name;
        this.description=description;
        this.ruleTemplateCategory=ruleTemplateCategory;
        this.ruleTemplateType=ruleTemplateType;
        this.payrollType=payrollType;
        this.payrollSystem=payrollSystem;

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

    public List<Long> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<Long> dayTypes) {
        this.dayTypes = dayTypes;
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

    public List<AccessGroup> getCalculateValueIfPlanned() {
        return calculateValueIfPlanned;
    }

    public void setCalculateValueIfPlanned(List<AccessGroup> calculateValueIfPlanned) {
        this.calculateValueIfPlanned = calculateValueIfPlanned;
    }

    public List<EmploymentType> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentType> employmentTypes) {
        this.employmentTypes = employmentTypes;
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

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public PlannedTimeWithFactor getPlannedTimeWithFactor() {
        return plannedTimeWithFactor;
    }

    public void setPlannedTimeWithFactor(PlannedTimeWithFactor plannedTimeWithFactor) {
        this.plannedTimeWithFactor = plannedTimeWithFactor;
    }

    public List<Long> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<Long> timeTypes) {
        this.timeTypes = timeTypes;
    }
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        if(this.getId()!=null)
        throw new UnsupportedOperationException("can't modified this property");
        this.createdBy = createdBy;
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CTARuleTemplate)) return false;

        CTARuleTemplate that = (CTARuleTemplate) o;

        return new EqualsBuilder()
                .append(payrollType, that.payrollType)
                .append(payrollSystem, that.payrollSystem)
                .append(calculationUnit, that.calculationUnit)
                .append(compensationTable, that.compensationTable)
                .append(calculateValueAgainst, that.calculateValueAgainst)
                .append(approvalWorkFlow, that.approvalWorkFlow)
                .append(dayTypes, that.dayTypes)
                .append(phaseInfo, that.phaseInfo)
                .append(budgetType, that.budgetType)
                .append(calculateValueIfPlanned, that.calculateValueIfPlanned)
                .append(employmentTypes, that.employmentTypes)
                .append(activityType, that.activityType)
                .append(planningCategory, that.planningCategory)
                .append(function, that.function)
                .append(plannedTimeWithFactor, that.plannedTimeWithFactor)
                .append(timeTypes, that.timeTypes)
                .append(createdBy, that.createdBy)
                .append(lastModifiedBy, that.lastModifiedBy)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(payrollType)
                .append(payrollSystem)
                .append(calculationUnit)
                .append(compensationTable)
                .append(calculateValueAgainst)
                .append(approvalWorkFlow)
                .append(dayTypes)
                .append(phaseInfo)
                .append(budgetType)
                .append(calculateValueIfPlanned)
                .append(employmentTypes)
                .append(activityType)
                .append(planningCategory)
                .append(function)
                .append(plannedTimeWithFactor)
                .append(timeTypes)
                .append(createdBy)
                .append(lastModifiedBy)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("payrollType", payrollType)
                .append("payrollSystem", payrollSystem)
                .append("calculationUnit", calculationUnit)
                .append("compensationTable", compensationTable)
                .append("calculateValueAgainst", calculateValueAgainst)
                .append("approvalWorkFlow", approvalWorkFlow)
                .append("dayTypes", dayTypes)
                .append("phaseInfo", phaseInfo)
                .append("budgetType", budgetType)
                .append("calculateValueIfPlanned", calculateValueIfPlanned)
                .append("employmentTypes", employmentTypes)
                .append("activityType", activityType)
                .append("planningCategory", planningCategory)
                .append("function", function)
                .append("plannedTimeWithFactor", plannedTimeWithFactor)
                .append("timeTypes", timeTypes)
                .append("createdBy", createdBy)
                .append("lastModifiedBy", lastModifiedBy)
                .toString();

    }
}
