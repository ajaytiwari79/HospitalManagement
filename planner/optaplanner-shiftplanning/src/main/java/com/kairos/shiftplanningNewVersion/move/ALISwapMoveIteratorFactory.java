package com.kairos.shiftplanningNewVersion.move;



import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ALISwapMoveIteratorFactory implements MoveIteratorFactory<StaffingLevelSolution> {

    @Override
    public long getSize(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        StaffingLevelSolution solution = scoreDirector.getWorkingSolution();
        int size = solution.getActivityLineIntervals().size();
        return solution.getShifts().size() * size;
    }

    @Override
    public Iterator<? extends Move<StaffingLevelSolution>> createOriginalMoveIterator(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ALISwapMoveIterator<? extends Move<StaffingLevelSolution>> createRandomMoveIterator(ScoreDirector<StaffingLevelSolution> scoreDirector, Random workingRandom) {
        StaffingLevelSolution solution = scoreDirector.getWorkingSolution();
        List<ALI> activityLineIntervals= solution.getActivityLineIntervals();
        List<ALI> possibleActivityLineIntervals= new ArrayList<>();
        LocalDate date=solution.getWeekDates().get(workingRandom.nextInt(solution.getWeekDates().size()));
        for (ALI activityLineInterval:activityLineIntervals) {
            if(activityLineInterval.getStart().toLocalDate().equals(date)){
                possibleActivityLineIntervals.add(activityLineInterval);
            }
        }
        ALISwapMoveIterator activityLineIntervalChangeMoveIterator = new ALISwapMoveIterator(possibleActivityLineIntervals,workingRandom);
        return activityLineIntervalChangeMoveIterator;
    }



}
