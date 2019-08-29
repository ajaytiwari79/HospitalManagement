package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.LocalDate;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ActivityLineIntervalSwapMoveIteratorFactory implements MoveIteratorFactory<ShiftRequestPhasePlanningSolution> {

    @Override
    public long getSize(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        ShiftRequestPhasePlanningSolution solution = scoreDirector.getWorkingSolution();
        int size = solution.getActivityLineIntervals().size();
        return solution.getShifts().size() * size;
    }

    @Override
    public Iterator<? extends Move<ShiftRequestPhasePlanningSolution>> createOriginalMoveIterator(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityLineIntervalSwapMoveIterator<? extends Move<ShiftRequestPhasePlanningSolution>> createRandomMoveIterator(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, Random workingRandom) {
        ShiftRequestPhasePlanningSolution solution = scoreDirector.getWorkingSolution();
        List<ActivityLineInterval> activityLineIntervals= solution.getActivityLineIntervals();
        List<ActivityLineInterval> possibleActivityLineIntervals= new ArrayList<>();
        LocalDate date=solution.getWeekDates().get(workingRandom.nextInt(solution.getWeekDates().size()));
        for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
            //if(activityLineInterval.getShift()==null)continue;
            if(activityLineInterval.getStart().toLocalDate().equals(date)){
                possibleActivityLineIntervals.add(activityLineInterval);
            }
        }
        //int[] randomRange= ShiftPlanningUtility.getRandomRange(possibleActivityLineIntervals.size(),workingRandom);
        //possibleActivityLineIntervals=possibleActivityLineIntervals.subList(randomRange[0],randomRange[1]);
        ActivityLineIntervalSwapMoveIterator activityLineIntervalChangeMoveIterator = new ActivityLineIntervalSwapMoveIterator(possibleActivityLineIntervals,workingRandom);
        return activityLineIntervalChangeMoveIterator;
    }



}
