package com.kairos.shiftplanningNewVersion.move;

import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.move.helper.ALIChangeMoveHelper;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ALIChangeMove extends AbstractMove<StaffingLevelSolution> {
    private ALI activityLineInterval;
    private Shift toShift;
    private List<ALI> exActivityLineIntervalsForThisShift;
    private Shift shiftForExIntervals;
    private static Logger log= LoggerFactory.getLogger(ALIChangeMove.class);
    public ALIChangeMove(ALI activityLineInterval, Shift toShift, List<ALI> exActivityLineIntervalsForThisShift,
                         Shift shiftForExIntervals) {
        this.activityLineInterval = activityLineInterval;
        this.toShift = toShift;
        this.exActivityLineIntervalsForThisShift = exActivityLineIntervalsForThisShift;
        this.shiftForExIntervals=shiftForExIntervals;
    }
    @Override
    protected AbstractMove<StaffingLevelSolution> createUndoMove(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        return new ALIChangeMove(activityLineInterval,activityLineInterval.getShift(), exActivityLineIntervalsForThisShift,toShift);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        ALIChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,activityLineInterval,toShift,exActivityLineIntervalsForThisShift,shiftForExIntervals);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<StaffingLevelSolution> scoreDirector) {

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
    public ALIChangeMove rebase(ScoreDirector<StaffingLevelSolution> destinationScoreDirector) {
        return new ALIChangeMove(destinationScoreDirector.lookUpWorkingObject(activityLineInterval),destinationScoreDirector.lookUpWorkingObject(toShift),rebaseList(exActivityLineIntervalsForThisShift,destinationScoreDirector),destinationScoreDirector.lookUpWorkingObject(shiftForExIntervals));
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalChangeMove{" +
                "activityLineInterval=" + activityLineInterval +"{"+activityLineInterval.getShift()+"}"
                +"-> toShift=" + toShift +
                '}';
    }
}
