package com.kairos.shiftplanning.constraints.activityconstraint;

import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
public class MaxShiftOfStaff implements Constraint {

    private int maxAllocationPerShift;
    private ScoreLevel level;
    private int weight;

    public MaxShiftOfStaff(int maxAllocationPerShift, ScoreLevel level, int weight) {
        this.maxAllocationPerShift = maxAllocationPerShift;
        this.level = level;
        this.weight = weight;
    }


    public int checkConstraints(Activity activity, ShiftImp shift){
        return 0;
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        Map<LocalDate,Long> shiftsCount = shifts.stream().collect(Collectors.groupingBy(shift->shift.getStartDate(),Collectors.counting()));
        int value = 0;
        for (Map.Entry<LocalDate, Long> localDateLongEntry : shiftsCount.entrySet()) {
            if(localDateLongEntry.getValue()>maxAllocationPerShift){
                value += (localDateLongEntry.getValue() - maxAllocationPerShift);
            }
        }
        return value;
    }
}