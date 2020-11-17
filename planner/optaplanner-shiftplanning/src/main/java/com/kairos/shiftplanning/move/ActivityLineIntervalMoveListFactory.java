package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftPlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivityLineIntervalMoveListFactory implements MoveListFactory<ShiftPlanningSolution> {
    @Override
    public List<? extends Move<ShiftPlanningSolution>> createMoveList(ShiftPlanningSolution solution) {
        List<ActivityLineInterval> activityLineIntervals= solution.getActivityLineIntervals();
        List<ActivityLineIntervalIteratorMove> possibleActivityLineIntervals= new ArrayList<>();
        List<ShiftImp> shifts= solution.getShifts();
        for (ShiftImp shift:shifts){
            for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
                if(activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                    ActivityLineIntervalIteratorMove activityLineIntervalWrapper = new ActivityLineIntervalIteratorMove(activityLineInterval,shift);
                    possibleActivityLineIntervals.add(activityLineIntervalWrapper);
                }
            }
        }
        return possibleActivityLineIntervals;
    }
}
