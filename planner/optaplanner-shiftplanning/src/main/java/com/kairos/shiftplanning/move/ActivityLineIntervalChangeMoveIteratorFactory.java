package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import com.kairos.shiftplanning.solution.ShiftPlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.*;

public class ActivityLineIntervalChangeMoveIteratorFactory implements MoveIteratorFactory<ShiftPlanningSolution> {

    @Override
    public long getSize(ScoreDirector<ShiftPlanningSolution> scoreDirector) {
        ShiftPlanningSolution solution = scoreDirector.getWorkingSolution();
        int size = solution.getActivityLineIntervals().size();
        return solution.getShifts().size() * size;
    }

    @Override
    public Iterator<? extends Move<ShiftPlanningSolution>> createOriginalMoveIterator(ScoreDirector<ShiftPlanningSolution> scoreDirector) {
        /*ShiftRequestPhasePlanningSolution solution = scoreDirector.getWorkingSolution();
        List<ActivityLineInterval> activityLineIntervals= solution.getActivityLineIntervals();
        List<ActivityLineIntervalWrapper> possibleActivityLineIntervals= new ArrayList<>(),nullActivityLineIntervals= new ArrayList<>();
        List<ShiftImp> workingShifts= solution.getShifts();
        List<ShiftImp> shifts=new ArrayList<>(workingShifts);
        ShiftImp shift=shifts.get(new Random().nextInt(shifts.size()));
        Activity activity=solution.getActivities().get(new Random().nextInt(solution.getActivities().size()));
        //for (ShiftImp shift:shifts){

            for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
                if(!activity.getId().equals(activityLineInterval.getActivity().getId())){// ||  !activityLineInterval.isRequired()
                    continue;
                }
                if(shift==null){
                    nullActivityLineIntervals.add(new ActivityLineIntervalWrapper(activityLineInterval,null));
                }else if(activityLineInterval.getStart().toLocalDate().equals(shift.getDate())){
                    possibleActivityLineIntervals.add(new ActivityLineIntervalWrapper(activityLineInterval,shift));
                    nullActivityLineIntervals.add(new ActivityLineIntervalWrapper(activityLineInterval,null));
                }
            }
        //}
        *//*if(new Random().nextBoolean()){
            Collections.reverse(possibleActivityLineIntervals);
        }*//*

        possibleActivityLineIntervals.addAll(nullActivityLineIntervals);
        Collections.shuffle(possibleActivityLineIntervals);
        ActivityLineIntervalChangeMoveIterator activityLineIntervalChangeMoveIterator = new ActivityLineIntervalChangeMoveIterator(possibleActivityLineIntervals);
        return activityLineIntervalChangeMoveIterator;*/
        throw new UnsupportedOperationException();
    }

    @Override
    public ActivityLineIntervalChangeMoveIterator<? extends Move<ShiftPlanningSolution>> createRandomMoveIterator(ScoreDirector<ShiftPlanningSolution> scoreDirector, Random workingRandom) {
        ShiftPlanningSolution solution = scoreDirector.getWorkingSolution();
        List<ActivityLineInterval> activityLineIntervals= solution.getActivityLineIntervals();
        List<ActivityLineIntervalWrapper> possibleActivityLineIntervals= new ArrayList<>(),nullActivityLineIntervals= new ArrayList<>();
        List<ShiftImp> workingShifts= solution.getShifts();
        List<ShiftImp> shifts=new ArrayList<>(workingShifts);
        //Pick a random date's shift and ALIs
        ShiftImp shift=shifts.get(new Random().nextInt(shifts.size()));
        Activity activity =solution.getActivitiesPerDay().get(shift.getStartDate()).get(workingRandom.nextInt(solution.getActivitiesPerDay().get(shift.getStartDate()).size()));
        for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
            if(!activity.getId().equals(activityLineInterval.getActivity().getId())){// ||  !activityLineInterval.isRequired()
                continue;
            }
            if(activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                possibleActivityLineIntervals.add(new ActivityLineIntervalWrapper(activityLineInterval,shift));
            }
        }
        Collections.shuffle(possibleActivityLineIntervals);
        int[] randomRange= ShiftPlanningUtility.getRandomRange(possibleActivityLineIntervals.size(),workingRandom);
        possibleActivityLineIntervals=possibleActivityLineIntervals.subList(randomRange[0],randomRange[1]);
        ActivityLineIntervalChangeMoveIterator activityLineIntervalChangeMoveIterator = new ActivityLineIntervalChangeMoveIterator(possibleActivityLineIntervals);
        return activityLineIntervalChangeMoveIterator;
    }



}
