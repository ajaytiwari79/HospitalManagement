package com.kairos.dto.activity.cta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.utils.TranslationUtil;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.shift.PlannedTime;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.enums.cta.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import static org.apache.commons.collections.CollectionUtils.containsAny;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
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
    private Set<BigInteger> activityIds;

    private Set<BigInteger> timeTypeIds;
    private Set<BigInteger> plannedTimeIds;

    private List<BigInteger> dayTypeIds;
    private List<DayOfWeek> days;
    private List<LocalDate> publicHolidays;
    @NotNull
    private BigInteger ruleTemplateCategoryId;
    private String ruleTemplateCategoryName;
    private UserInfo lastModifiedBy;
    private ConditionalCompensation conditionalCompensation;
    private Long countryId;
    private Long unitId;
    private Map<String, TranslationInfo> translations;
    private boolean notApplicableForSunday; // this is for getting  compensation on sunday


    public void setPhaseInfo(List<CTARuleTemplatePhaseInfo> phaseInfo) {
        this.phaseInfo = Optional.ofNullable(phaseInfo).orElse(new ArrayList<>());
    }

    public List<BigInteger> getDayTypeIds() {
        this.dayTypeIds = isNull(dayTypeIds) ? new ArrayList<>() : this.dayTypeIds;
        return dayTypeIds;
    }

    public void setDayTypeIds(List<BigInteger> dayTypeIds) {
        this.dayTypeIds = isNull(dayTypeIds) ? new ArrayList<>() : dayTypeIds;
    }

    public void setActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = isNull(activityIds) ? new HashSet<>() : activityIds;
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

    private boolean isPhaseValid(BigInteger shiftPhaseId){
        return this.getPhaseInfo().stream().filter(p -> shiftPhaseId.equals(p.getPhaseId())).findFirst().isPresent();
    }

    private boolean isActivityAndTimeTypeAndPlannedTimeValid(Set<BigInteger> activityIds,Set<BigInteger> timeTypeIds,List<PlannedTime> plannedTimes){
        return (containsAny(this.getActivityIds(),activityIds) || containsAny(this.getTimeTypeIds(),timeTypeIds)) && CollectionUtils.containsAny(this.getPlannedTimeIds(),plannedTimes.stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
    }

    private boolean isEmployementTypeValid(Long employmentId){
        return this.getEmploymentTypes().contains(employmentId);
    }

    public boolean isRuleTemplateValid(Long employmentTypeId,BigInteger shiftPhaseId,Set<BigInteger> activityIds,Set<BigInteger> timeTypeIds,List<PlannedTime> plannedTimes){
        return isPhaseValid(shiftPhaseId) && isEmployementTypeValid(employmentTypeId) && (isActivityAndTimeTypeAndPlannedTimeValid(activityIds,timeTypeIds,plannedTimes) || this.getCalculationFor().equals(FUNCTIONS));
    }

    public String getName() {
        return TranslationUtil.getName(translations,name);
    }

    public String getDescription() {
        return  TranslationUtil.getDescription(translations,description);
    }

    public Map<String, TranslationInfo> getTranslations() {
        return isNull(translations) ? new HashMap<>() : translations;
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

