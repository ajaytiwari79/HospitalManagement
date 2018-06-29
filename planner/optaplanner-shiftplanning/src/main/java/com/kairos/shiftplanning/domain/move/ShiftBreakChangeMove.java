package com.kairos.shiftplanning.domain.move;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.Shift;
import com.kairos.shiftplanning.domain.ShiftBreak;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.move.helper.ShiftBreakChangeMoveHelper;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ShiftBreakChangeMove extends AbstractMove<BreaksIndirectAndActivityPlanningSolution> {
    private ShiftBreak shiftBreak;
    private DateTime breakTime;
    private List<ActivityLineInterval> activityLineIntervals;
    private ShiftRequestPhase shift;

    public ShiftBreakChangeMove(ShiftBreak shiftBreak, DateTime breakTime, List<ActivityLineInterval> activityLineIntervals, ShiftRequestPhase shift) {
        this.shiftBreak = shiftBreak;
        this.breakTime = breakTime;
        this.activityLineIntervals = activityLineIntervals;
        this.shift = shift;
    }

    @Override
    protected AbstractMove<BreaksIndirectAndActivityPlanningSolution> createUndoMove(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        return new ShiftBreakChangeMove(shiftBreak,shiftBreak.getStartTime(),activityLineIntervals,shiftBreak.getShift());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        ShiftBreakChangeMoveHelper.assignShiftBreakToShift(scoreDirector,shiftBreak,breakTime,activityLineIntervals,shift);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        return true;
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Arrays.asList(shiftBreak);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Arrays.asList(breakTime);
    }

    @Override
    public String toString() {
        return "Shift:"+shiftBreak.getShift().getDate()+"["+shiftBreak.getDuration()+"]"+"{"+shiftBreak.getStartTime()+"}"+"->{"+breakTime+"}";
    }
}
