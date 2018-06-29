package com.kairos.shiftplanning.domain.move;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.ArrayList;
import java.util.List;

public class ActivityLineIntervalMoveListFactory implements MoveListFactory<ShiftRequestPhasePlanningSolution> {
    @Override
    public List<? extends Move<ShiftRequestPhasePlanningSolution>> createMoveList(ShiftRequestPhasePlanningSolution solution) {
        List<ActivityLineInterval> activityLineIntervals= solution.getActivityLineIntervals();
        List<ActivityLineIntervalIteratorMove> possibleActivityLineIntervals= new ArrayList<>();
        List<ShiftRequestPhase> shifts= solution.getShifts();
        for (ShiftRequestPhase shift:shifts){
            for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
                if(activityLineInterval.getStart().toLocalDate().equals(shift.getDate())){
                    ActivityLineIntervalIteratorMove activityLineIntervalWrapper = new ActivityLineIntervalIteratorMove(activityLineInterval,shift);
                    possibleActivityLineIntervals.add(activityLineIntervalWrapper);
                }
            }
        }
        return possibleActivityLineIntervals;
    }
}
