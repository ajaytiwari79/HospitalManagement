package com.kairos.shiftplanning.constraints.activityconstraint;


import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
* This class represent constraint
* Presence and Absence type of shifts
* should not happen at same time
* */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PresenceAndAbsenceAtSameTime implements ConstraintHandler {

    private ScoreLevel level;
    private int weight;

    public PresenceAndAbsenceAtSameTime(ScoreLevel level, int weight) {
        this.level = level;
        this.weight = weight;
    }

    public int checkConstraints(Activity activity, ShiftImp shift){
        Set<TimeTypeEnum> timeTypeEnumSet = shift.getActivityLineIntervals().stream().map(activityLineInterval -> activityLineInterval.getActivity().getTimeType().getTimeTypeEnum()).collect(Collectors.toSet());
        return timeTypeEnumSet.size();
    }

    @Override
    public int checkConstraints(List<ShiftImp> shifts) {
        shifts.sort(Comparator.comparing(ShiftImp::getStartDate));
        List<ShiftActivity> shiftActivities = shifts.stream().flatMap(shiftImp -> shiftImp.getShiftActivities().stream()).sorted(Comparator.comparing(ShiftActivity::getStartDate)).collect(Collectors.toList());
        int contraintPenality = 0;
        for (int i = 1; i < shiftActivities.size(); i++) {
            ShiftActivity shiftActivity = shiftActivities.get(i - 1);
            ShiftActivity nextShiftActivity = shiftActivities.get(i);
            if(shiftActivity.getInterval().overlaps(nextShiftActivity.getInterval()) && shiftActivity.getActivity().isTypePresence() && nextShiftActivity.getActivity().isTypeAbsence()){
                contraintPenality++;
            }
        }
        return contraintPenality;
    }

    @Override
    public int verifyConstraints(Unit unit, Shift shiftImp, List<Shift> shiftImps){return 0;};
}
