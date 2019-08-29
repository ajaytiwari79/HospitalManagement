package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalChangeMoveHelper;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ActivityLineIntervalChangeMove  extends AbstractMove<ShiftRequestPhasePlanningSolution> {
    private ActivityLineInterval activityLineInterval;
    private ShiftImp toShift;
    private List<ActivityLineInterval> exActivityLineIntervalsForThisShift;
    private ShiftImp shiftForExIntervals;
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMove.class);
    public ActivityLineIntervalChangeMove(ActivityLineInterval activityLineInterval, ShiftImp toShift, List<ActivityLineInterval> exActivityLineIntervalsForThisShift,
                                          ShiftImp shiftForExIntervals) {
        this.activityLineInterval = activityLineInterval;
        this.toShift = toShift;
        this.exActivityLineIntervalsForThisShift = exActivityLineIntervalsForThisShift;
        this.shiftForExIntervals=shiftForExIntervals;
    }
    @Override
    protected AbstractMove<ShiftRequestPhasePlanningSolution> createUndoMove(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        return new ActivityLineIntervalChangeMove(activityLineInterval,activityLineInterval.getShift(), exActivityLineIntervalsForThisShift,toShift);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        ActivityLineIntervalChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,activityLineInterval,toShift,exActivityLineIntervalsForThisShift,shiftForExIntervals);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {

        return !Objects.equals(activityLineInterval.getShift(), toShift);// && !ShiftPlanningUtility.intervalOverlapsBreak(toShift,activityLineInterval.getStart());
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Collections.singletonList(activityLineInterval);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(toShift);
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalChangeMove{" +
                "activityLineInterval=" + activityLineInterval +"{"+activityLineInterval.getShift()+"}"
                +"-> toShift=" + toShift +
                '}';
    }
}
