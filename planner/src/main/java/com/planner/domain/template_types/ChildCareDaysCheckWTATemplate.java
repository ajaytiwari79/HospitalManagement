package com.planner.domain.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.enums.DurationType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftOperationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta.WTABaseRuleTemplate;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getIntervalByActivity;

/**
 * Created by pavan on 23/4/18.
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChildCareDaysCheckWTATemplate extends WTABaseRuleTemplate {
    private List<BigInteger> activityIds = new ArrayList<>();
    private float recommendedValue;
    private CutOffIntervalUnit cutOffIntervalUnit;
    private int transferLeaveCount;
    private int borrowLeaveCount;
    private List<ActivityCutOffCount> activityCutOffCounts = new ArrayList<>();


    public ChildCareDaysCheckWTATemplate() {
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
    }

    public void validateRules(Unit unit, ShiftImp shiftImp,List<ShiftImp> shiftImps) {
        if (!isDisabled() && validateRulesChildCareDayCheck(unit.getActivityWrapperMap()) && CollectionUtils.containsAny(activityIds,shiftImp.getActivityIds()) && !ShiftOperationType.DELETE.equals(unit.getShiftOperationType())) {
            WorkTimeAgreementBalancesCalculationService workTimeAgreementService= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(WorkTimeAgreementBalancesCalculationService.class);
            if (isCollectionNotEmpty(shiftImp.getEmployee().getSeniorAndChildCareDays().getChildCareDays())) {
                long leaveCount = calculateChildCareDaysLeaveCount(shiftImp.getEmployee().getSeniorAndChildCareDays().getChildCareDays(), unit.getStaffChildAges());
                DateTimeInterval dateTimeInterval = getIntervalByActivity(shiftImp, activityIds);
                if (isNotNull(dateTimeInterval)) {
                    List<ShiftImp> shifts = shiftImps.stream().filter(shift -> CollectionUtils.containsAny(shift.getActivityIds(), activityIds) && dateTimeInterval.contains(shift.getStartDate())).collect(Collectors.toList());
                    ActivityCutOffCount activityLeaveCount = this.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(), activityCutOffCount.getEndDate()).containsAndEqualsEndDate(shiftImp.getStart().toDate())).findFirst().orElse(new ActivityCutOffCount());
                    if (leaveCount + activityLeaveCount.getTransferLeaveCount() - activityLeaveCount.getBorrowLeaveCount() < (shifts.size() + 1)) {
                        boolean isLeaveAvailable = workTimeAgreementService.isLeaveCountAvailable(unit.getActivityWrapperMap(), activityIds.get(0), unit.getShift(), dateTimeInterval, unit.getLastPlanningPeriodEndDate(), WTATemplateType.WTA_FOR_CARE_DAYS, leaveCount);

                    }
                }
            }
        }

    }


    private boolean validateRulesChildCareDayCheck(Map<BigInteger, ActivityWrapper> activityWrapperMap) {
        for(BigInteger activityId : activityWrapperMap.keySet()){
            if(!TimeTypeEnum.PAID_BREAK.equals(activityWrapperMap.get(activityId).getTimeTypeInfo().getSecondLevelType()) && isNotNull(activityWrapperMap.get(activityId).getActivity().getRulesActivityTab().getCutOffIntervalUnit())){
                return true;
            }
        }
        return false;
    }

    public long calculateChildCareDaysLeaveCount(List<CareDaysDTO> careDaysDTOS, List<Integer> staffChildAges){
        long leaveCount = 0L;
        if (isCollectionNotEmpty(staffChildAges)) {
            for (Integer staffChildAge : staffChildAges) {
                for (CareDaysDTO careDaysDTO : careDaysDTOS) {
                    if (staffChildAge >= careDaysDTO.getFrom() && isNull(careDaysDTO.getT633o()) || staffChildAge < careDaysDTO.getTo()) {
                        leaveCount += careDaysDTO.getLeavesAllowed();
                        break;
                    }
                }
            }
        }
        return leaveCount;
    }

    public ChildCareDaysCheckWTATemplate(String name, boolean disabled, String description) {
        super(name, description);
        this.wtaTemplateType = WTATemplateType.CHILD_CARE_DAYS_CHECK;
        this.disabled = disabled;
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        ChildCareDaysCheckWTATemplate childCareDaysCheckWTATemplate = (ChildCareDaysCheckWTATemplate) wtaBaseRuleTemplate;
        return (this != childCareDaysCheckWTATemplate) && !(
                Float.compare(childCareDaysCheckWTATemplate.recommendedValue, recommendedValue) == 0 &&
                        Objects.equals(activityIds, childCareDaysCheckWTATemplate.activityIds) &&
                        cutOffIntervalUnit == childCareDaysCheckWTATemplate.cutOffIntervalUnit && Objects.equals(this.phaseTemplateValues,childCareDaysCheckWTATemplate.phaseTemplateValues));
    }

}
