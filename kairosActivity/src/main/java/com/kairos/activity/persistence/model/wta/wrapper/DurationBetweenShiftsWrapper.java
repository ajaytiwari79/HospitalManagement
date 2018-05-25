package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DurationBetweenShiftsWrapper implements RuleTemplateWrapper{

    private DurationBetweenShiftsWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public DurationBetweenShiftsWrapper(DurationBetweenShiftsWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        int timefromPrevShift = 0;
        List<ShiftQueryResultWithActivity> shifts = filterShifts(infoWrapper.getShifts(), wtaTemplate.getTimeTypeIds(), wtaTemplate.getPlannedTimeIds(), null);
        shifts = (List<ShiftQueryResultWithActivity>) shifts.stream().filter(shift1 -> DateUtils.getZoneDateTime(shift1.getEndDate()).isBefore(DateUtils.getZoneDateTime(infoWrapper.getShift().getStartDate()))).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
        if (shifts.size() > 0) {
            ZonedDateTime prevShiftEnd = DateUtils.getZoneDateTime(shifts.size() > 1 ? shifts.get(shifts.size() - 1).getEndDate() : shifts.get(0).getEndDate());
            timefromPrevShift = new DateTimeInterval(prevShiftEnd, DateUtils.getZoneDateTime(infoWrapper.getShift().getStartDate())).getMinutes();
            Integer[] limitAndCounter = getValueByPhase(infoWrapper,wtaTemplate.getPhaseTemplateValues(),wtaTemplate.getId());
            if (!isValid(wtaTemplate.getMinMaxSetting(), limitAndCounter[0], timefromPrevShift)) {
                if(limitAndCounter[1]!=null) {
                    int counterValue =  limitAndCounter[1] - 1;
                    if(counterValue<0){
                        new InvalidRequestException(wtaTemplate.getName() + " is Broken");
                        infoWrapper.getCounterMap().put(wtaTemplate.getId(), infoWrapper.getCounterMap().getOrDefault(wtaTemplate.getId(), 0) + 1);
                    }
                }else {
                    new InvalidRequestException(wtaTemplate.getName() + " is Broken");
                }
            }
        }
        return "";
    }

}
