package com.kairos.persistence.model.cta;

import com.kairos.dto.activity.cta.CTARuleTemplatePhaseInfo;
import com.kairos.dto.activity.cta.CompensationTable;
import com.kairos.dto.activity.cta.PlannedTimeWithFactor;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.enums.cta.*;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

/**
 * @author pradeep
 * @date - 30/7/18
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<BigInteger> activityIds;
    private Set<BigInteger> timeTypeIds;
    private Set<BigInteger> plannedTimeIds;
    private List<Long> dayTypeIds;
    //it describe that this template is scheduledHoursTemplate or not
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;
    private Long countryId;

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = isNotNull(activityIds) ? activityIds : new HashSet<>();
    }

    public void setTimeTypeIds(Set<BigInteger> timeTypeIds) {
        this.timeTypeIds = isNotNull(timeTypeIds) ? timeTypeIds : new HashSet<>();
    }

    public void setPlannedTimeIds(Set<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = isNotNull(plannedTimeIds) ? plannedTimeIds : new HashSet<>();
    }
    public void setPhaseInfo(List<CTARuleTemplatePhaseInfo> phaseInfo) {
        this.phaseInfo = isNotNull(phaseInfo) ? phaseInfo : new ArrayList<>();
    }

    public void setDayTypeIds(List<Long> dayTypeIds) {
        this.dayTypeIds = isNotNull(dayTypeIds) ? dayTypeIds : new ArrayList<>();
    }

    public boolean isCalculatedValueChanged(CTARuleTemplate ctaRuleTemplate){
        return (this!=ctaRuleTemplate) && !(disabled == ctaRuleTemplate.disabled &&
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
}

