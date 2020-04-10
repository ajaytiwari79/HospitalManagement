package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

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

    private boolean shiftTimeContainsInNightInterval(DateTime shiftTime){
        return new Interval(shiftTime.withTimeAtStartOfDay().plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute()),shiftTime.withTimeAtStartOfDay().plusDays(1).plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute())).contains(shiftTime);
    }
}