package com.kairos.shiftplanning.constraints.unitconstraint;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

import static com.kairos.commons.utils.DateUtils.getStartOfDay;

@Getter
@Setter
@AllArgsConstructor
public class DislikeNightShiftsForNonNightWorkers implements Constraint {
    private ScoreLevel level;
    private int weight;
    private TimeSlot nightTimeSlot;

    private boolean shiftTimeContainsInNightInterval(ZonedDateTime shiftTime){
        return new DateTimeInterval(getStartOfDay(shiftTime).plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute()),getStartOfDay(shiftTime.plusDays(1).plusHours(nightTimeSlot.getStartHour()).plusMinutes(nightTimeSlot.getStartMinute()))).contains(shiftTime);
    }


    @Override
    public int checkConstraints(Activity activity, ShiftImp shift) {
        int penality = 0;
        if(shift.getEmployee().isNightWorker() && shiftTimeContainsInNightInterval(shift.getStart())){
            penality = 1;
        }
        return penality;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        return 0;
    }
}
