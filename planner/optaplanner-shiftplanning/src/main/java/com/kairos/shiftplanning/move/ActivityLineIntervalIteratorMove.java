package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Arrays;
import java.util.Collection;

public class ActivityLineIntervalIteratorMove extends AbstractMove<ShiftRequestPhasePlanningSolution> {
    private final ActivityLineInterval activityLineInterval;
    private final ShiftImp shiftImp;

    public ActivityLineIntervalIteratorMove(ActivityLineInterval activityLineInterval, ShiftImp shiftImp) {
        this.activityLineInterval = activityLineInterval;
        this.shiftImp = shiftImp;
    }

    @Override
    protected AbstractMove<ShiftRequestPhasePlanningSolution> createUndoMove(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        return new ActivityLineIntervalIteratorMove(activityLineInterval,activityLineInterval.getShift());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        scoreDirector.beforeVariableChanged(activityLineInterval, "shift");
        activityLineInterval.setShift(shiftImp);
        scoreDirector.afterVariableChanged(activityLineInterval, "shift");
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        return true;
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Arrays.asList(activityLineInterval);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Arrays.asList(shiftImp);
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalIteratorMove{" +
                "activityLineInterval=" + activityLineInterval +" "+activityLineInterval.getShift()+
                "-> shiftImp=" + shiftImp +
                '}';
    }
}