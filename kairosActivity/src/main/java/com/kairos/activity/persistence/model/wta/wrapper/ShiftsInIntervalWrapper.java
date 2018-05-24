package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ShiftsInIntervalWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.TimeInterval;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ShiftsInIntervalWrapper implements RuleTemplateWrapper{

    private ShiftsInIntervalWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public ShiftsInIntervalWrapper(ShiftsInIntervalWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }

    @Override
    public String isSatisfied() {
        if(wtaTemplate.isDisabled()){
            TimeInterval timeInterval = getTimeSlotByPartOfDay(wtaTemplate.getPartOfDays(),infoWrapper.getTimeSlotWrappers(),infoWrapper.getShift());
            if(timeInterval!=null) {
                DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), wtaTemplate.getIntervalUnit(), wtaTemplate.getIntervalLength());
                List<ShiftQueryResultWithActivity> shifts = filterShifts(infoWrapper.getShifts(), wtaTemplate.getTimeTypeIds(), wtaTemplate.getPlannedTimeIds(), wtaTemplate.getActivityIds());
                shifts = getShiftsByInterval(dateTimeInterval, shifts, timeInterval);
                Integer[] limitAndCounter = getValueByPhase(infoWrapper,wtaTemplate.getPhaseTemplateValues(),wtaTemplate.getId());
                if (!isValid(wtaTemplate.getMinMaxSetting(), limitAndCounter[0], shifts.size())) {
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
        }
        return "";
    }

    public static void checkConstraints(ShiftQueryResultWithActivity shift, List<ShiftQueryResultWithActivity> shifts, ShiftsInIntervalWTATemplate ruleTemplate) {

    }
}
