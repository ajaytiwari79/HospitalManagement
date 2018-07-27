package com.kairos.persistence.model.agreement.cta;

import com.kairos.persistence.model.auth.User;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import com.kairos.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.user.country.agreement.cta.CalculationFor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
public class CTARuleTemplate extends RuleTemplate {

    //    private CTARuleTemplateType ruleTemplateType;
    private String ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    @Relationship(type = HAS_COMPENSATION_TABLE)
    private CompensationTable compensationTable;
    @Relationship(type = BELONGS_TO)
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    //    @Relationship(type = BELONGS_TO)
//    List<CTARuleTemplateDayType>calculateOnDayTypes=new ArrayList<>(); // Need to confirm, Can have different values for dayType and countryHoliday
    @Relationship(type = BELONGS_TO)
    private List<CTARuleTemplatePhaseInfo> phaseInfo = new ArrayList<>(); //Query beforeStart
    private BudgetType budgetType;

    private List<CalculateValueIfPlanned> calculateValueIfPlanned = new ArrayList<>();
    @Relationship(type = HAS_EMPLOYMENT_TYPE)

    private List<EmploymentType> employmentTypes = new ArrayList<>();
    private PlanningCategory planningCategory;
    private List<Long> staffFunctions = new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private PlannedTimeWithFactor plannedTimeWithFactor;
    @Relationship(type = BELONGS_TO)
    private User createdBy;
    @Relationship(type = BELONGS_TO)
    private User lastModifiedBy;

    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private List<Long> activityIds;
    private Set<Long> timeTypeIds;
    private Set<Long> plannedTimeIds;
    private List<Long> dayTypeIds;
    //it describe that this template is scheduledHoursTemplate or not
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;

    public CTARuleTemplate() {

    }

    public CTARuleTemplate(String name, String description, String payrollType, String payrollSystem) {
        this.name = name;
        this.description = description;
        this.ruleTemplateCategory = ruleTemplateCategory;
        this.payrollType = payrollType;
        this.payrollSystem = payrollSystem;

    }

    public void cloneCTARuleTemplate() {
        this.setId(null);
       /* if(doUpdate){

        } else {

        }*/
        if (this.getCompensationTable() != null && this.getCompensationTable().getCompensationTableInterval() != null) {
            for (CompensationTableInterval compensationTableInterval : this.getCompensationTable().getCompensationTableInterval()) {
                compensationTableInterval.setId(null);
            }
            this.getCompensationTable().setId(null);
        }

        if (this.getCalculateValueAgainst() != null) {
            this.getCalculateValueAgainst().getFixedValue().setId(null);
            this.getCalculateValueAgainst().setId(null);
        }

        /*if(this.getCalculateOnDayTypes() != null){
            for (CTARuleTemplateDayType ctaRuleTemplateDayType : this.getCalculateOnDayTypes()) {
                ctaRuleTemplateDayType.setId(null);
                //DayType not clone
                for (CountryHolidayCalender countryHolidayCalender : ctaRuleTemplateDayType.getCountryHolidayCalenders()) {
                    countryHolidayCalender.setId(null);
                }
            }
        }*/
        if (this.getPhaseInfo() != null) {
            for (CTARuleTemplatePhaseInfo ctaRuleTemplatePhaseInfo : this.getPhaseInfo()) {
                ctaRuleTemplatePhaseInfo.setId(null);
            }
        }
        /*if(this.getActivityType() != null){
            this.getActivityType().setId(null);
        }*/
        if (this.getPlannedTimeWithFactor() != null) {
            this.getPlannedTimeWithFactor().setId(null);
        }

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

    public List<EmploymentType> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentType> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public void addEmploymentType(EmploymentType employmentType) {

        this.employmentTypes = Optional.ofNullable(employmentTypes).orElse(new ArrayList<>());
        this.employmentTypes.add(employmentType);
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        if (this.getId() != null)
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
                .append(phaseInfo, that.phaseInfo)
                .append(budgetType, that.budgetType)
                .append(calculateValueIfPlanned, that.calculateValueIfPlanned)
                .append(employmentTypes, that.employmentTypes)
                .append(planningCategory, that.planningCategory)
                .append(staffFunctions, that.staffFunctions)
                .append(plannedTimeWithFactor, that.plannedTimeWithFactor)
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
                .append(phaseInfo)
                .append(budgetType)
                .append(calculateValueIfPlanned)
                .append(employmentTypes)
                .append(planningCategory)
                .append(staffFunctions)
                .append(plannedTimeWithFactor)
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
                .append("phaseInfo", phaseInfo)
                .append("budgetType", budgetType)
                .append("calculateValueIfPlanned", calculateValueIfPlanned)
                .append("employmentTypes", employmentTypes)
                .append("planningCategory", planningCategory)
                .append("staffFunction", staffFunctions)
                .append("plannedTimeWithFactor", plannedTimeWithFactor)
                .append("createdBy", createdBy)
                .append("lastModifiedBy", lastModifiedBy)
                .toString();

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
}
