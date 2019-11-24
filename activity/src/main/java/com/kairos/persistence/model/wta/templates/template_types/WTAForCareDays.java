package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.commons.config.ApplicationContextProviderNonManageBean;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.activity_tabs.CutOffIntervalUnit;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.dto.activity.wta.templates.ActivityCutOffCount;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.service.wta.WorkTimeAgreementBalancesCalculationService;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getShiftsByIntervalAndActivityIds;


/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
public class WTAForCareDays extends WTABaseRuleTemplate{

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    private CutOffIntervalUnit cutOffIntervalUnit;

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }


    // getActivityCutOffCounts().get(0) change and get count by date
    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        WorkTimeAgreementBalancesCalculationService workTimeAgreementService= ApplicationContextProviderNonManageBean.getApplicationContext().getBean(WorkTimeAgreementBalancesCalculationService.class);
        if(!isDisabled()) {
            Map<BigInteger,ActivityCareDayCount> careDayCountMap = careDaysCountMap();
            for (ShiftActivityDTO shiftActivityDTO : infoWrapper.getShift().getActivities()) {
                if(careDayCountMap.containsKey(shiftActivityDTO.getActivityId())) {
                Activity activity = infoWrapper.getActivityWrapperMap().get(shiftActivityDTO.getActivityId()).getActivity();
                    ActivityCareDayCount careDayCount = careDayCountMap.get(activity.getId());
                    List<ShiftWithActivityDTO> shifts = getShiftsByIntervalAndActivityIds(activity, infoWrapper.getShift().getStartDate(), infoWrapper.getShifts(), Arrays.asList(careDayCount.getActivityId()));
                    ActivityCutOffCount leaveCount=careDayCount.getActivityCutOffCounts().stream().filter(activityCutOffCount -> new DateTimeInterval(activityCutOffCount.getStartDate(),activityCutOffCount.getEndDate()).containsAndEqualsEndDate(asDate(asLocalDate(infoWrapper.getShift().getStartDate())))).findFirst().orElse(new ActivityCutOffCount());
                    if (leaveCount.getCount()+leaveCount.getTransferLeaveCount() < (shifts.size()+1)) {
                        boolean isLeaveAvailable = workTimeAgreementService.isLeaveCountAvailable(infoWrapper.getActivityWrapperMap(), careDayCount.getActivityId(), infoWrapper.getShift(), new DateTimeInterval(leaveCount.getStartDate(), leaveCount.getEndDate()), infoWrapper.getLastPlanningPeriodEndDate(), WTATemplateType.WTA_FOR_CARE_DAYS,leaveCount.getCount());
                        if (!isLeaveAvailable) {
                            WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                                    new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false, null,
                                            DurationType.DAYS, String.valueOf(leaveCount.getCount()+leaveCount.getTransferLeaveCount()));
                            infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                            break;
                        }
                    }
                }
            }
        }
    }

    public Map<BigInteger,ActivityCareDayCount> careDaysCountMap(){
        return this.careDayCounts.stream().collect(Collectors.toMap(ActivityCareDayCount::getActivityId,v->v));
    }

    @Override
    public boolean isCalculatedValueChanged(WTABaseRuleTemplate wtaBaseRuleTemplate) {
        WTAForCareDays wtaForCareDays = (WTAForCareDays) wtaBaseRuleTemplate;
        return (this != wtaForCareDays) && !(Objects.equals(careDayCounts, wtaForCareDays.careDayCounts));
    }

}
