package com.kairos.shiftplanning.constraints.activityConstraint;


import com.kairos.enums.TimeTypeEnum;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

/*
* This class represent constraint
* Presence and Absence type of shifts
* should not happen at same time
* */
@Getter
@Setter
@NoArgsConstructor
public class PresenceAndAbsenceAtSameTime implements Constraint {

    private ScoreLevel level;
    private int weight;

    public int checkConstraints(Activity activity, ShiftImp shift){
        Set<TimeTypeEnum> timeTypeEnumSet = shift.getActivityLineIntervals().stream().map(activityLineInterval -> activityLineInterval.getActivity().getTimeType().getTimeTypeEnum()).collect(Collectors.toSet());
        return timeTypeEnumSet.size();
    }
}
