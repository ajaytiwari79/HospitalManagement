package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.NumberOfPartOfDayShiftsWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isNightShift;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class NumberOfPartOfDayShiftsWrapper implements RuleTemplateWrapper{


    private NumberOfPartOfDayShiftsWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(List<ShiftQueryResultWithActivity> shifts, NumberOfPartOfDayShiftsWTATemplate ruleTemplate,TimeSlotWrapper timeSlotWrapper){
        if(shifts.size()>0) {
            int count = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumNumberOfNightsInfo();
            for (ShiftQueryResultWithActivity shift : shifts) {
                if (isNightShift(shift, timeSlotWrapper)) {
                    count++;
                }
            }
            if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getNoOfPartOfDayWorked(), count)) {
                new InvalidRequestException("");
            }
        }
    }

}
