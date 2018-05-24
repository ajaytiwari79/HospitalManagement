package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ShiftLengthWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.TimeInterval;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ShiftLengthWrapper implements RuleTemplateWrapper{


    private ShiftLengthWTATemplate wtaTemplate;
    private RuleTemplateSpecificInfo infoWrapper;

    @Override
    public String isSatisfied() {
        TimeInterval timeInterval = getTimeSlotByPartOfDay(wtaTemplate.getPartOfDays(),infoWrapper.getTimeSlotWrappers(),infoWrapper.getShift());
        if(timeInterval!=null){
            if(isValidForDay(wtaTemplate.getDayTypeIds(),infoWrapper)) {
                Integer[] limitAndCounter = getValueByPhase(infoWrapper,wtaTemplate.getPhaseTemplateValues(),wtaTemplate.getId());
                if (!isValid(wtaTemplate.getMinMaxSetting(), limitAndCounter[0], infoWrapper.getShift().getMinutes())) {
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

    public ShiftLengthWrapper(ShiftLengthWTATemplate wtaTemplate, RuleTemplateSpecificInfo infoWrapper) {
        this.wtaTemplate = wtaTemplate;
        this.infoWrapper = infoWrapper;
    }
}
