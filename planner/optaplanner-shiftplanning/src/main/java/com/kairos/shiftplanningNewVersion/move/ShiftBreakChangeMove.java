package com.kairos.shiftplanningNewVersion.move;


import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.move.helper.ShiftBreakChangeMoveHelper;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ShiftBreakChangeMove extends AbstractMove<BreaksIndirectAndActivityPlanningSolution> {
    private ShiftBreak shiftBreak;
    private ZonedDateTime breakTime;
    private List<ALI> activityLineIntervals;
    private Shift shift;

    public ShiftBreakChangeMove(ShiftBreak shiftBreak, ZonedDateTime breakTime, List<ALI> activityLineIntervals, Shift shift) {
        this.shiftBreak = shiftBreak;
        this.breakTime = breakTime;
        this.activityLineIntervals = activityLineIntervals;
        this.shift = shift;
    }

    @Override
    protected AbstractMove<BreaksIndirectAndActivityPlanningSolution> createUndoMove(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        return null;//new ShiftBreakChangeMove(shiftBreak,shiftBreak.getStartTime(),activityLineIntervals,shiftBreak.getShift());
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
        return "Shift:"+shiftBreak.getShift().getStartDate()+"["+shiftBreak.getDuration()+"]"+"{"+shiftBreak.getStartTime()+"}"+"->{"+breakTime+"}";
    }
}
