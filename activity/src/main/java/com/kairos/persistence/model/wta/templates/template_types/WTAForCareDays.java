package com.kairos.persistence.model.wta.templates.template_types;

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.WorkTimeAgreementRuleViolation;
import com.kairos.dto.activity.wta.templates.ActivityCareDayCount;
import com.kairos.enums.DurationType;
import com.kairos.enums.wta.WTATemplateType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.wrapper.wta.RuleTemplateSpecificInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.utils.worktimeagreement.RuletemplateUtils.getShiftsByIntervalAndActivityIds;


/**
 * @author pradeep
 * @date - 10/10/18
 */
@Getter
@Setter
public class WTAForCareDays extends WTABaseRuleTemplate{

    private List<ActivityCareDayCount> careDayCounts = new ArrayList<>();

    public WTAForCareDays(String name, String description) {
        super(name, description);
    }

    public WTAForCareDays() {
        wtaTemplateType = WTATemplateType.WTA_FOR_CARE_DAYS;
    }


    // getActivityCutOffCounts().get(0) change and get count by date
    @Override
    public void validateRules(RuleTemplateSpecificInfo infoWrapper) {
        if(!isDisabled()) {
            Map<BigInteger,ActivityCareDayCount> careDayCountMap = careDaysCountMap();
            for (ShiftActivityDTO shiftActivityDTO : infoWrapper.getShift().getActivities()) {
                if(careDayCountMap.containsKey(shiftActivityDTO.getActivityId())) {
                Activity activity = infoWrapper.getActivityWrapperMap().get(shiftActivityDTO.getActivityId()).getActivity();
                    ActivityCareDayCount careDayCount = careDayCountMap.get(activity.getId());
                    List<ShiftWithActivityDTO> shifts = getShiftsByIntervalAndActivityIds(activity, infoWrapper.getShift().getStartDate(), infoWrapper.getShifts(), Arrays.asList(careDayCount.getActivityId()));
                    if (careDayCount.getActivityCutOffCounts().get(0).getCount() < (shifts.size()+1)) {
                        WorkTimeAgreementRuleViolation workTimeAgreementRuleViolation =
                                new WorkTimeAgreementRuleViolation(this.id, this.name, null, true, false,null,

                                        DurationType.DAYS,String.valueOf(careDayCount.getActivityCutOffCounts().get(0).getCount()));
                        infoWrapper.getViolatedRules().getWorkTimeAgreements().add(workTimeAgreementRuleViolation);
                        break;
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
