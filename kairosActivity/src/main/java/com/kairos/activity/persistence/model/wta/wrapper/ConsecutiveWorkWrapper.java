package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ConsecutiveWorkWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.TimeInterval;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ConsecutiveWorkWrapper implements RuleTemplateWrapper{

    private ConsecutiveWorkWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    public ConsecutiveWorkWrapper(ConsecutiveWorkWTATemplate ruleTemplate, RuleTemplateSpecificInfo ruleTemplateSpecificInfo) {
        this.wtaTemplate = ruleTemplate;
        this.infoWrapper = ruleTemplateSpecificInfo;
    }


    @Override
    public String isSatisfied() {
        if(wtaTemplate.isDisabled()) {
            if ((wtaTemplate.getTimeTypeIds().contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) || wtaTemplate.getPlannedTimeIds().contains(infoWrapper.getShift().getActivity().getBalanceSettingsActivityTab().getPresenceTypeId()))) {
                TimeInterval timeInterval = getTimeSlotByPartOfDay(wtaTemplate.getPartOfDays(), infoWrapper.getTimeSlotWrappers(), infoWrapper.getShift());
                if (timeInterval != null) {
                    List<ShiftQueryResultWithActivity> shiftQueryResultWithActivities = filterShifts(infoWrapper.getShifts(), wtaTemplate.getTimeTypeIds(), wtaTemplate.getPlannedTimeIds(), null);
                    DateTimeInterval dateTimeInterval = getIntervalByRuleTemplate(infoWrapper.getShift(), wtaTemplate.getIntervalUnit(), wtaTemplate.getIntervalLength());
                    shiftQueryResultWithActivities = getShiftsByInterval(dateTimeInterval, shiftQueryResultWithActivities, timeInterval);
                    shiftQueryResultWithActivities.add(infoWrapper.getShift());
                    List<LocalDate> shiftDates = getSortedAndUniqueDates(shiftQueryResultWithActivities, infoWrapper.getShift());
                    int consecutiveDays = getConsecutiveDays(shiftDates);
                    Integer[] limitAndCounter = getValueByPhase(infoWrapper, wtaTemplate.getPhaseTemplateValues(), wtaTemplate.getId());
                    if (!isValid(wtaTemplate.getMinMaxSetting(), limitAndCounter[0], consecutiveDays)) {
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
            }
        }
        return "";
    }

}