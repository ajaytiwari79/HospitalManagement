package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.client.dto.TimeSlotWrapper;
import com.kairos.activity.custom_exception.InvalidRequestException;
import com.kairos.activity.persistence.model.wta.templates.template_types.ShiftLengthWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateUtils;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValid;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isValidForPartOfDay;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ShiftLengthWrapper implements RuleTemplateWrapper{


    private ShiftLengthWTATemplate wtaTemplate;
    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;
    private List<TimeSlotWrapper> timeSlotWrappers;
    private List<DayOfWeek> dayOfWeeks;

    @Override
    public boolean isSatisfied() {
        if(isValidForPartOfDay(shift,wtaTemplate.getPartOfDays(),timeSlotWrappers)){
            int shiftDay = DateUtils.getZoneDateTime(shift.getStartDate()).get(ChronoField.DAY_OF_WEEK);
            Optional<DayOfWeek> dayOfWeek = dayOfWeeks.stream().filter(day -> day.getValue()== shiftDay).findAny();
            if(dayOfWeek.isPresent()) {
                if (!isValid(wtaTemplate.getMinMaxSetting(), (int) wtaTemplate.getTimeLimit(), shift.getMinutes())) {
                    new InvalidRequestException(wtaTemplate.getName()+" is Broken");
                }
            }
        }
        return false;
    }


}
