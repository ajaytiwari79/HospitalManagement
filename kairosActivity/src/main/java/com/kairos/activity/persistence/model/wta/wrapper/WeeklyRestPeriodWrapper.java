package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.WeeklyRestPeriodWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.TimeInterval;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getValueByPhase;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class WeeklyRestPeriodWrapper implements RuleTemplateWrapper {

    private RuleTemplateSpecificInfo infoWrapper;
    private WeeklyRestPeriodWTATemplate wtaTemplate;

    public WeeklyRestPeriodWrapper(WeeklyRestPeriodWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        TimeInterval timeInterval = getTimeSlotByPartOfDay(wtaTemplate.getPartOfDays(), infoWrapper.getTimeSlotWrappers(), infoWrapper.getShift());
        if (timeInterval != null) {
            DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), wtaTemplate.getIntervalUnit(), wtaTemplate.getIntervalLength());
            int totalRestingTime = getTotalRestingTime(infoWrapper.getShifts(), dateTimeInterval);
            Integer[] limitAndCounter = getValueByPhase(infoWrapper, wtaTemplate.getPhaseTemplateValues(), wtaTemplate.getId());
            if (!isValid(wtaTemplate.getMinMaxSetting(), limitAndCounter[0], totalRestingTime)) {
                if (limitAndCounter[1] != null) {
                    int counterValue = limitAndCounter[1] - 1;
                    if (counterValue < 0) {
                        new InvalidRequestException(wtaTemplate.getName() + " is Broken");
                        infoWrapper.getCounterMap().put(wtaTemplate.getId(), infoWrapper.getCounterMap().getOrDefault(wtaTemplate.getId(), 0) + 1);
                    }
                } else {
                    new InvalidRequestException(wtaTemplate.getName() + " is Broken");
                }
            }

        }
        return "";
    }

    public int getTotalRestingTime(List<ShiftQueryResultWithActivity> shifts, DateTimeInterval dateTimeInterval) {
        if (shifts.size() < 2) return 0;
        int totalRestTime = dateTimeInterval.getMinutes();
        for (ShiftQueryResultWithActivity shift : shifts) {
            totalRestTime -= shift.getMinutes();
        }
        return totalRestTime;
    }
}
