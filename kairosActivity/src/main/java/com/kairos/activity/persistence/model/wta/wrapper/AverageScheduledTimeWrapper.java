package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.AverageScheduledTimeWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getIntervalByRuleTemplate;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class AverageScheduledTimeWrapper implements RuleTemplateWrapper{

    private AverageScheduledTimeWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public AverageScheduledTimeWrapper(AverageScheduledTimeWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }


    @Override
    public String isSatisfied() {
            int totalScheduledTime = 0;
            DateTimeInterval interval = getIntervalByRuleTemplate(infoWrapper.getShift(),wtaTemplate.getIntervalUnit(),wtaTemplate.getIntervalLength());
            for (ShiftQueryResultWithActivity shift1:infoWrapper.getShifts()) {
                if(interval.overlaps(shift1.getInterval())){
                    totalScheduledTime+=interval.overlap(shift1.getInterval()).getMinutes();
                }
            }
             int scheduledTime = totalScheduledTime>wtaTemplate.getMaximumAvgTime()?totalScheduledTime-(int)wtaTemplate.getMaximumAvgTime():0;
        return "";
    }
}