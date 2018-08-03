package com.kairos.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.CalculationUnit;
import com.kairos.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.user.country.agreement.cta.CalculationFor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CTARuleTemplateDTO {
    //    @NotNull
    public BigInteger id;
    @NotEmpty(message = "error.cta.ruleTemplate.name.notEmpty")
    @NotNull(message = "error.cta.ruleTemplate.name.notNull")
    public String name;
    public String description;
    public boolean disabled;
    @NotNull
    public BigInteger ruleTemplateCategory;
    private String ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    //    private List<CTARuleTemplateDayTypeDTO>calculateOnDayTypes=new ArrayList<>();
    private List<CTARuleTemplatePhaseInfo> phaseInfo = new ArrayList<>();
    private BudgetType budgetType;
    private List<CalculateValueIfPlanned> calculateValueIfPlanned = new ArrayList<>();
    private List<Long> employmentTypes = new ArrayList<>();
    //    private ActivityType activityType;
    private PlanningCategory planningCategory;
    private List<Long> staffFunctions = new ArrayList<>();
    private PlannedTimeWithFactor plannedTimeWithFactor;
    private boolean calculateScheduledHours;
    //    private List<Long> timeTypes =new ArrayList<>();
    private CalculationFor calculationFor;

    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private List<BigInteger> activityIds;

    /* @Modified by VIPUL
    * for KP-2927
    * */
    private Set<BigInteger> timeTypeIds;
    private Set<BigInteger> plannedTimeIds;

    private List<Long> dayTypeIds;

    public CTARuleTemplateDTO() {
    }


    public boolean isCalculateScheduledHours() {
        return calculateScheduledHours;
    }

    public void setCalculateScheduledHours(boolean calculateScheduledHours) {
        this.calculateScheduledHours = calculateScheduledHours;
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

    public BigInteger getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(BigInteger ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
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

    /*public List<CTARuleTemplateDayTypeDTO> getCalculateOnDayTypes() {
        return calculateOnDayTypes;
    }

    public void setCalculateOnDayTypes(List<CTARuleTemplateDayTypeDTO> calculateOnDayTypes) {
        this.calculateOnDayTypes = calculateOnDayTypes;
    }*/

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

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    /*public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }*/

    public List<Long> getDayTypeIds() {
        return dayTypeIds;
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = dayTypeIds;
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

/*    public List<Long> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<Long> timeTypes) {
        this.timeTypes = timeTypes;
    }*/

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
        this.activityIds = activityIds;
    }

    public Set<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(Set<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public Set<BigInteger> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(Set<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public CalculationFor getCalculationFor() {
        return calculationFor;
    }

    public void setCalculationFor(CalculationFor calculationFor) {
        this.calculationFor = calculationFor;
    }



    public CTARuleTemplateDTO(String name, String description, String payrollType, String payrollSystem) {
        this.name = name;
        this.description = description;
        this.ruleTemplateCategory = ruleTemplateCategory;
        this.payrollType = payrollType;
        this.payrollSystem = payrollSystem;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("name", name)
                .append("description", description)
                .append("disabled", disabled)
                .append("ruleTemplateCategory", ruleTemplateCategory)
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
}

