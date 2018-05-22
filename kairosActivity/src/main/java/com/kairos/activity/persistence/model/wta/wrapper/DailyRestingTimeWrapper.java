package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.DailyRestingTimeWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getSortedIntervals;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DailyRestingTimeWrapper implements RuleTemplateWrapper{

    private DailyRestingTimeWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(List<ShiftQueryResultWithActivity> shifts,DailyRestingTimeWTATemplate ruleTemplate){
        if(shifts.size()>2) {
            List<DateTimeInterval> intervals = getSortedIntervals(shifts);
            int restingTimeUnder = 0;
            for (int i = 1; i < intervals.size(); i++) {
                long lastEnd = intervals.get(i - 1).getEndMillis();
                long thisStart = intervals.get(i).getStartMillis();
                long totalRest = (thisStart - lastEnd) / 60000;
                if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getContinuousDayRestHours(), (int) totalRest)) {
                    new InvalidRequestException("");
                }
            }
        }
    }

}