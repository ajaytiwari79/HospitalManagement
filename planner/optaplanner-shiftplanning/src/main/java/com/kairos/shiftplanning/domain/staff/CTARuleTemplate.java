package com.kairos.shiftplanning.domain.staff;

import com.kairos.dto.activity.cta.CTARuleTemplatePhaseInfo;
import com.kairos.dto.activity.cta.CompensationTable;
import com.kairos.dto.activity.cta.PlannedTimeWithFactor;
import com.kairos.dto.user.country.agreement.cta.CalculateValueIfPlanned;
import com.kairos.dto.user.country.agreement.cta.CalculationFor;
import com.kairos.enums.cta.*;
import com.kairos.shiftplanning.domain.shift.PlannedTime;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.dto.user.country.agreement.cta.CalculationFor.FUNCTIONS;

/**
 * @author pradeep
 * @date - 30/7/18
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CTARuleTemplate {

    private BigInteger id;
    private String name;
    private String description;
    private boolean disabled;
    private BigInteger ruleTemplateCategoryId;
    private String ruleTemplateType;
    private String payrollType;
    private String payrollSystem;
    private CalculationUnit calculationUnit;
    private CompensationTable compensationTable;
    private CalculateValueAgainst calculateValueAgainst;
    private ApprovalWorkFlow approvalWorkFlow;
    @Builder.Default
    private List<CTARuleTemplatePhaseInfo> phaseInfo = new ArrayList<>();
    private BudgetType budgetType;
    @Builder.Default
    private List<CalculateValueIfPlanned> calculateValueIfPlanned = new ArrayList<>();
    @Builder.Default
    private Set<Long> employmentTypes = new HashSet<>();
    private PlanningCategory planningCategory;
    @Builder.Default
    private Set<Long> staffFunctions = new HashSet<>();
    private PlannedTimeWithFactor plannedTimeWithFactor;

    private ActivityTypeForCostCalculation activityTypeForCostCalculation;
    private Set<BigInteger> activityIds;
    private Set<BigInteger> timeTypeIds;
    private Set<BigInteger> plannedTimeIds;
    private Set<Long> dayTypeIds;
    //it describe that this template is scheduledHoursTemplate or not
    private boolean calculateScheduledHours;
    private CalculationFor calculationFor;
    private Long countryId;

    public Set<BigInteger> getActivityIds(Set<BigInteger> activityIds) {
        this.activityIds = isNotNull(activityIds) ? activityIds : new HashSet<>();
        return this.activityIds;
    }

    public Set<BigInteger> getTimeTypeIds(Set<BigInteger> timeTypeIds) {
        this.timeTypeIds = isNotNull(timeTypeIds) ? timeTypeIds : new HashSet<>();
        return this.timeTypeIds;
    }

    public Set<BigInteger> getPlannedTimeIds(Set<BigInteger> plannedTimeIds) {
        this.plannedTimeIds = isNotNull(plannedTimeIds) ? plannedTimeIds : new HashSet<>();
        return this.plannedTimeIds;
    }

    public List<CTARuleTemplatePhaseInfo> getPhaseInfo(List<CTARuleTemplatePhaseInfo> phaseInfo) {
        this.phaseInfo = isNotNull(phaseInfo) ? phaseInfo : new ArrayList<>();
        return this.phaseInfo;
    }

    public Set<Long> getDayTypeIds(Set<Long> dayTypeIds) {
        this.dayTypeIds = isNotNull(dayTypeIds) ? dayTypeIds : new HashSet<>();
        return this.dayTypeIds;
    }

    public boolean isRuleTemplateValid(Long employmentTypeId,BigInteger shiftPhaseId,BigInteger activityId,BigInteger timeTypeId,List<PlannedTime> plannedTimes){
        return isPhaseValid(shiftPhaseId) && isEmployementTypeValid(employmentTypeId) && (isActivityAndTimeTypeAndPlannedTimeValid(activityId,timeTypeId,plannedTimes) || this.getCalculationFor().equals(FUNCTIONS));
    }

    private boolean isPhaseValid(BigInteger shiftPhaseId){
        return this.getPhaseInfo().stream().filter(p -> shiftPhaseId.equals(p.getPhaseId())).findFirst().isPresent();
    }

    private boolean isActivityAndTimeTypeAndPlannedTimeValid(BigInteger activityId,BigInteger timeTypeId,List<PlannedTime> plannedTimes){
        return (this.getActivityIds().contains(activityId) || this.getTimeTypeIds().contains(timeTypeId)) && CollectionUtils.containsAny(this.getPlannedTimeIds(),plannedTimes.stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
    }

    private boolean isEmployementTypeValid(Long employmentId){
        return this.getEmploymentTypes().contains(employmentId);
    }

}

