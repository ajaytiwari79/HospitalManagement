package com.kairos.shiftplanningNewVersion.move;




import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;

import java.util.ArrayList;
import java.util.List;

public class ALIMoveListFactory implements MoveListFactory<StaffingLevelSolution> {
    @Override
    public List<? extends Move<StaffingLevelSolution>> createMoveList(StaffingLevelSolution solution) {
        List<ALI> activityLineIntervals= solution.getActivityLineIntervals();
        List<ALIIteratorMove> possibleActivityLineIntervals= new ArrayList<>();
        List<Shift> shifts= solution.getShifts();
        for (Shift shift:shifts){
            for (ALI activityLineInterval:activityLineIntervals) {
                if(activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                    ALIIteratorMove activityLineIntervalWrapper = new ALIIteratorMove(activityLineInterval,shift);
                    possibleActivityLineIntervals.add(activityLineIntervalWrapper);
                }
            }
        }
        return possibleActivityLineIntervals;
    }
}
