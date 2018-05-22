package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.WeeklyRestPeriodWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplate;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class WeeklyRestPeriodWrapper implements RuleTemplateWrapper{

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;


    private WeeklyRestPeriodWTATemplate wtaTemplate;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(ShiftQueryResultWithActivity shift,List<ShiftQueryResultWithActivity> shifts,WeeklyRestPeriodWTATemplate ruleTemplate){
        if(shifts.size()>2) {
            int totalRestTime = getIntervalByRuleTemplate(shift, ruleTemplate.getIntervalUnit(), ruleTemplate.getIntervalLength()).getMinutes();
            for (ShiftQueryResultWithActivity shift1 : shifts) {
                totalRestTime -= shift1.getMinutes();
            }
            if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getContinuousWeekRest(), totalRestTime)) {
                new InvalidRequestException("");
            }
        }
    }
}
