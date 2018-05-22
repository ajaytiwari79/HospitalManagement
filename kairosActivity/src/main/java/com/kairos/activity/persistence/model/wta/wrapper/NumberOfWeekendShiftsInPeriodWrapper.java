package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.NumberOfWeekendShiftsInPeriodWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplate;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class NumberOfWeekendShiftsInPeriodWrapper implements RuleTemplateWrapper{

    private NumberOfWeekendShiftsInPeriodWTATemplate wtaTemplate;

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(ShiftQueryResultWithActivity shift,List<ShiftQueryResultWithActivity> shifts,NumberOfWeekendShiftsInPeriodWTATemplate ruleTemplate){
        DateTimeInterval interval = getIntervalByRuleTemplate(shift,ruleTemplate.getIntervalUnit(),ruleTemplate.getIntervalLength());
        int weekendShifts=(int) shifts.stream().filter(s->interval.contains(s.getStartDate())).count();
        if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getFromTime(), weekendShifts)) {
            new InvalidRequestException("");
        }
    }
}
