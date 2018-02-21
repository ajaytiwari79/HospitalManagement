package com.kairos.persistence.model.user.agreement.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.cta.*;
import com.kairos.response.dto.web.cta.CTARuleTemplateDayTypeDTO;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class CTARuleTemplateQueryResult {
    @NotNull
    public Long id;
    public String name;
    public String description;
    public boolean disabled;
    @NotNull
    public Long ruleTemplateCategory;
    private CTARuleTemplateType ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private Map<String,Object> compensationTable;
    private Map<String,Object> calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    private List<CTARuleTemplateDayTypeDTO> calculateOnDayTypes=new ArrayList<>();
    private List<CTARuleTemplatePhaseInfo> phaseInfo=new ArrayList<>();
    private BudgetType budgetType;
    private List<CalculateValueIfPlanned> calculateValueIfPlanned =new ArrayList<>();
    private List<Long> employmentTypes =new ArrayList<>();
//    private ActivityType activityType;
    private PlanningCategory planningCategory;
    private List<StaffFunction> staffFunctions=new ArrayList<>();
    private PlannedTimeWithFactor plannedTimeWithFactor;
//    private List<Long> timeTypes =new ArrayList<>();
    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private List<Long> activityIds;
    private Long timeTypeId;
    private Long plannedTimeId;

    public CTARuleTemplateQueryResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getRuleTemplateCategory() {
        return ruleTemplateCategory;
    }

    public void setRuleTemplateCategory(Long ruleTemplateCategory) {
        this.ruleTemplateCategory = ruleTemplateCategory;
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

    public Map<String,Object> getCompensationTable() {
        return compensationTable;
    }

    public void setCompensationTable(Map<String,Object> compensationTable) {
        this.compensationTable = compensationTable;
    }

    public Map<String, Object> getCalculateValueAgainst() {
        return calculateValueAgainst;
    }

    public void setCalculateValueAgainst(Map<String, Object> calculateValueAgainst) {
        this.calculateValueAgainst = calculateValueAgainst;
    }

    public ApprovalWorkFlow getApprovalWorkFlow() {
        return approvalWorkFlow;
    }

    public void setApprovalWorkFlow(ApprovalWorkFlow approvalWorkFlow) {
        this.approvalWorkFlow = approvalWorkFlow;
    }

    public List<CTARuleTemplateDayTypeDTO> getCalculateOnDayTypes() {
        return calculateOnDayTypes;
    }

    public void setCalculateOnDayTypes(List<CTARuleTemplateDayTypeDTO> calculateOnDayTypes) {
        this.calculateOnDayTypes = calculateOnDayTypes;
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

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

/*    public ActivityType getActivityType() {
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

    public List<StaffFunction> getStaffFunctions() {
        return staffFunctions;
    }

    public void setStaffFunctions(List<StaffFunction> staffFunctions) {
        this.staffFunctions = staffFunctions;
    }

    public PlannedTimeWithFactor getPlannedTimeWithFactor() {
        return plannedTimeWithFactor;
    }

    public void setPlannedTimeWithFactor(PlannedTimeWithFactor plannedTimeWithFactor) {
        this.plannedTimeWithFactor = plannedTimeWithFactor;
    }

    /*public List<Long> getTimeTypes() {
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

    public List<Long> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<Long> activityIds) {
        this.activityIds = activityIds;
    }

    public Long getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(Long timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public Long getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(Long plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
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
                .append("ctaRuleTemplateDayTypeDTO", calculateOnDayTypes)
                .append("phaseInfo", phaseInfo)
                .append("budgetType", budgetType)
                .append("calculateValueIfPlanned", calculateValueIfPlanned)
                .append("employmentTypes", employmentTypes)
//                .append("activityType", activityType)
                .append("planningCategory", planningCategory)
                .append("staffFunction", staffFunctions)
                .append("plannedTimeWithFactor", plannedTimeWithFactor)
//                .append("timeTypes", timeTypes)
                .toString();
    }
}
