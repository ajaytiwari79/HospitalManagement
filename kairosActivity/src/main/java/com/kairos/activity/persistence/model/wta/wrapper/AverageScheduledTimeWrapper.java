package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.AverageScheduledTimeWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.WTARuleTemplateValidatorUtility;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class AverageScheduledTimeWrapper implements RuleTemplateWrapper{

    private AverageScheduledTimeWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;


    @Override
    public boolean isSatisfied() {
            int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
            DateTimeInterval interval = getIntervalByRuleTemplate(shift,wtaTemplate.getIntervalUnit(),wtaTemplate.getIntervalLength());
            for (ShiftQueryResultWithActivity shift1:shifts) {
                if(interval.overlaps(shift1.getInterval())){
                    totalScheduledTime+=interval.overlap(shift1.getInterval()).getMinutes();
                }
            }
             int scheduledTime = totalScheduledTime>wtaTemplate.getMaximumAvgTime()?totalScheduledTime-(int)wtaTemplate.getMaximumAvgTime():0;
        return false;
    }
}