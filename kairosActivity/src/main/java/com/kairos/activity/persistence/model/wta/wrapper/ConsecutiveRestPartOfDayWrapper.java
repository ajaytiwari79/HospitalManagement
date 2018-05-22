package com.kairos.activity.persistence.model.wta.wrapper;

import com.kairos.activity.persistence.model.wta.templates.template_types.ConsecutiveRestPartOfDayWTATemplate;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import com.kairos.activity.util.DateTimeInterval;
import com.kairos.activity.util.DateUtils;
import com.kairos.activity.util.WTARuleTemplateValidatorUtility;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.getSortedDates;
import static com.kairos.activity.util.WTARuleTemplateValidatorUtility.isNightShift;

/**
 * @author pradeep
 * @date - 22/5/18
 */

public class ConsecutiveRestPartOfDayWrapper implements RuleTemplateWrapper{


    private ConsecutiveRestPartOfDayWTATemplate wtaTemplate;

    private List<ShiftQueryResultWithActivity> shifts;
    private ShiftQueryResultWithActivity shift;


    @Override
    public boolean isSatisfied() {
       /* if(shifts.size()<2) return true;
        WTARuleTemplateValidatorUtility.sortShifts(shifts);
        List<LocalDate> dates=getSortedDates(shifts);

        int l=1;
        int consDays=0;
        int totalRestUnder=0;
        while (l<dates.size()){
            if(dates.get(l-1).equals(dates.get(l).minusDays(1)) && isNightShift(shifts.get(l),timeSlotWrapper)&& isNightShift(shifts.get(l-1),timeSlotWrapper)){
                consDays++;
            }else{
                consDays=0;
            }
            if(consDays>=nightsWorked){
                ZonedDateTime start= DateUtils.getZoneDateTime(shifts.get(l-1).getEndDate());
                ZonedDateTime end=DateUtils.getZoneDateTime(shifts.get(l).getStartDate());
                int diff=new DateTimeInterval(start,end).getMinutes()- minimumRest;//FIXME
                totalRestUnder+=diff;
                consDays=0;
            }
            l++;
        }*/
        return false;
    }
}