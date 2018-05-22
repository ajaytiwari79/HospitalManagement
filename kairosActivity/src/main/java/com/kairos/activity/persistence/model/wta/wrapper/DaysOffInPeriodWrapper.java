package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.DaysOffInPeriodWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getSortedDates;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DaysOffInPeriodWrapper implements RuleTemplateWrapper{

    private DaysOffInPeriodWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }


    public static void checkConstraints(List<ShiftQueryResultWithActivity> shifts, DaysOffInPeriodWTATemplate ruleTemplate){
        int shiftsNum=getSortedDates(shifts).size();
        if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getDaysLimit(), shiftsNum)) {
            new InvalidRequestException("");
        }
    }
}