package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.user.access_permission.AccessGroup;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.country.CountryHolidayCalender;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.country.TimeType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
public class CTARuleTemplate extends RuleTemplate{

    private CTARuleTemplateType ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    @Relationship(type = HAS_COMPENSATION_TABLE)
    private CompensationTable compensationTable;
    @Relationship(type = BELONGS_TO)
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    @Relationship(type = BELONGS_TO)
    List<CTARuleTemplateDayType>calculateOnDayTypes=new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private List<CTARuleTemplatePhaseInfo>phaseInfo=new ArrayList<>();
    private BudgetType budgetType;
    @Relationship(type = HAS_ACCESS_GROUP)
    private List<AccessGroup> calculateValueIfPlanned=new ArrayList<>();
    @Relationship(type = HAS_EMPLOYMENT_TYPE)
    private List<EmploymentType> employmentTypes=new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private ActivityType activityType;
    private PlanningCategory planningCategory;
    private List<StaffFunction> staffFunctions=new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private PlannedTimeWithFactor plannedTimeWithFactor;
    @Relationship(type = HAS_TIME_TYPES)
    private List<TimeType>timeTypes=new ArrayList<>();
    @Relationship(type = BELONGS_TO)
    private User createdBy;
    @Relationship(type = BELONGS_TO)
    private User lastModifiedBy;

    public CTARuleTemplate() {

    }

    public CTARuleTemplate(String name, String description, CTARuleTemplateType ruleTemplateType, String payrollType, String payrollSystem) {
        this.name=name;
        this.description=description;
        this.ruleTemplateCategory=ruleTemplateCategory;
        this.ruleTemplateType=ruleTemplateType;
        this.payrollType=payrollType;
        this.payrollSystem=payrollSystem;

    }

    public  void cloneCTARuleTemplate(){
        this.setId(null);
        if(this.getCompensationTable() != null){
            for (CompensationTableInterval compensationTableInterval : this.getCompensationTable().getCompensationTableInterval()) {
                compensationTableInterval.setId(null);
            }
            this.getCompensationTable().setId(null);
        }

        if(this.getCalculateValueAgainst() != null ){
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
        if(this.getPhaseInfo() != null){
            for (CTARuleTemplatePhaseInfo ctaRuleTemplatePhaseInfo : this.getPhaseInfo()) {
                ctaRuleTemplatePhaseInfo.setId(null);
            }
        }
        if(this.getActivityType() != null){
            this.getActivityType().setId(null);
        }
        if(this.getPlannedTimeWithFactor() != null){
            this.getPlannedTimeWithFactor().setId(null);
        }

    }

    public CTARuleTemplate buildCTARuleTemplateFromDTO(CTARuleTemplateDTO ctaRuleTemplateDTO){
        return this;
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

    public List<CTARuleTemplateDayType> getCalculateOnDayTypes() {
        return calculateOnDayTypes;
    }

    public void setCalculateOnDayTypes(List<CTARuleTemplateDayType> calculateOnDayTypes) {
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

    public CTARuleTemplateType getRuleTemplateType() {
        return ruleTemplateType;
    }

    public void setRuleTemplateType(CTARuleTemplateType ruleTemplateType) {
        this.ruleTemplateType = ruleTemplateType;
    }


    public List<TimeType> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeType> timeTypes) {
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
                .append(phaseInfo, that.phaseInfo)
                .append(budgetType, that.budgetType)
                .append(calculateValueIfPlanned, that.calculateValueIfPlanned)
                .append(employmentTypes, that.employmentTypes)
                .append(activityType, that.activityType)
                .append(planningCategory, that.planningCategory)
                .append(staffFunctions, that.staffFunctions)
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
                .append(phaseInfo)
                .append(budgetType)
                .append(calculateValueIfPlanned)
                .append(employmentTypes)
                .append(activityType)
                .append(planningCategory)
                .append(staffFunctions)
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
                .append("phaseInfo", phaseInfo)
                .append("budgetType", budgetType)
                .append("calculateValueIfPlanned", calculateValueIfPlanned)
                .append("employmentTypes", employmentTypes)
                .append("activityType", activityType)
                .append("planningCategory", planningCategory)
                .append("staffFunction", staffFunctions)
                .append("plannedTimeWithFactor", plannedTimeWithFactor)
                .append("timeTypes", timeTypes)
                .append("createdBy", createdBy)
                .append("lastModifiedBy", lastModifiedBy)
                .toString();

    }

}
