package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.ZonedDateTime;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.getStartOfDay;

@Getter
@Setter
@AllArgsConstructor
public class MaxLengthOfShiftInNightTimeSlot implements Constraint {

    private ScoreLevel level;
    private int weight;
    private TimeSlot nightTimeSlot;
    private int length;

    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        int penality = 0;
        if(shiftTimeContainsInNightInterval(shift.getStart()) && (length < shift.getMinutes())){
            penality = (length - shift.getMinutes()) * weight;
        }
        return penality;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }

    private boolean shiftTimeContainsInNightInterval(ZonedDateTime shiftTime){
        return new DateTimeInterval(getStartOfDay(shiftTime).plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute()),getStartOfDay(shiftTime).plusDays(1).plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute())).contains(shiftTime);
    }
}
