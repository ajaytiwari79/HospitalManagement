package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ConsecutiveWorkWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;

import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.*;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ConsecutiveWorkWrapper implements RuleTemplateWrapper{

    private ConsecutiveWorkWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;
    private List<TimeSlotWrapper> timeSlotWrappers;

    @Override
    public boolean isSatisfied() {
        if((wtaTemplate.getTimeTypeIds().contains(shift.getActivity().getBalanceSettingsActivityTab().getTimeTypeId()) || wtaTemplate.getPlannedTimeIds().contains(shift.getActivity().getBalanceSettingsActivityTab().getPresenceTypeId())) && isValidForPartOfDay(shift,wtaTemplate.getPartOfDays(),timeSlotWrappers)) {
            List<ShiftQueryResultWithActivity> shifts = filterShifts(shifts,wtaTemplate.getTimeTypeIds(),wtaTemplate.getPlannedTimeIds(),null);
            int consecutiveDays = getConsecutiveDays(getSortedAndUniqueDates(shifts));
            if (!isValid(wtaTemplate.getMinMaxSetting(), (int) wtaTemplate.getLimitCount(), consecutiveDays)) {
                new InvalidRequestException(wtaTemplate.getName()+" is Broken");
            }
        }
        return true;
    }

}