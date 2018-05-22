package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.DurationBetweenShiftsWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getShiftStartTimeComparator;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class DurationBetweenShiftsWrapper implements RuleTemplateWrapper{

    private DurationBetweenShiftsWTATemplate wtaTemplate;

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;

    @Override
    public boolean isSatisfied() {
        return false;
    }

    public static void checkConstraints(List<ShiftQueryResultWithActivity> shifts, ShiftQueryResultWithActivity shift,DurationBetweenShiftsWTATemplate ruleTemplate) {
        boolean isValid = false;
        int timefromPrevShift = 0;
        shifts = (List<ShiftQueryResultWithActivity>) shifts.stream().filter(shift1 -> DateUtils.getZoneDateTime(shift1.getEndDate()).isBefore(DateUtils.getZoneDateTime(shift.getStartDate()))).sorted(getShiftStartTimeComparator()).collect(Collectors.toList());
        if (shifts.size() > 0) {
            ZonedDateTime prevShiftEnd = DateUtils.getZoneDateTime(shifts.size() > 1 ? shifts.get(shifts.size() - 1).getEndDate() : shifts.get(0).getEndDate());
            timefromPrevShift = new DateTimeInterval(prevShiftEnd, DateUtils.getZoneDateTime(shift.getStartDate())).getMinutes();
            /*if(timefromPrevShift==0 && shift.getStartDate().getDayOfWeek()==1){
                timefromPrevShift = new DateTimeInterval(shift.getEmployee().getPrevShiftEnd(), shift.getStartDate()).getMinutes();
            }*/
        }
        if (!isValid(ruleTemplate.getMinMaxSetting(), (int) ruleTemplate.getDurationBetweenShifts(), (int) timefromPrevShift)) {
            new InvalidRequestException("");
        }
    }
}
