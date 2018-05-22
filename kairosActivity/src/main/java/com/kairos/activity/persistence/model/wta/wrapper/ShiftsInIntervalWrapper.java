package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ShiftsInIntervalWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplate;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ShiftsInIntervalWrapper implements RuleTemplateWrapper{

    private ShiftsInIntervalWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(ShiftQueryResultWithActivity shift,List<ShiftQueryResultWithActivity> shifts,ShiftsInIntervalWTATemplate ruleTemplate){
        int shiftCount = 0;
        DateTimeInterval interval = getIntervalByRuleTemplate(shift,ruleTemplate.getIntervalUnit(),ruleTemplate.getIntervalLength());
        for (ShiftQueryResultWithActivity shift1:shifts) {
            if(interval.contains(shift1.getStartDate()))
                shiftCount++;
        }
        if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getShiftsLimit(), shiftCount)) {
            new InvalidRequestException("");
        }
    }
}
