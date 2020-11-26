package com.kairos.shiftplanningNewVersion.move;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.move.helper.ALIWrapper;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.*;

public class ALIChangeMoveIteratorFactory implements MoveIteratorFactory<StaffingLevelSolution> {

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
    public ALIChangeMoveIterator<? extends Move<StaffingLevelSolution>> createRandomMoveIterator(ScoreDirector<StaffingLevelSolution> scoreDirector, Random workingRandom) {
        StaffingLevelSolution solution = scoreDirector.getWorkingSolution();
        List<ALI> activityLineIntervals= solution.getActivityLineIntervals();
        List<ALIWrapper> possibleActivityLineIntervals= new ArrayList<>();
        List<Shift> workingShifts= solution.getShifts();
        List<Shift> shifts=new ArrayList<>(workingShifts);
        //Pick a random date's shift and ALIs
        Shift shift=shifts.get(new Random().nextInt(shifts.size()));
        Activity activity =solution.getActivitiesPerDay().get(shift.getStartDate()).get(workingRandom.nextInt(solution.getActivitiesPerDay().get(shift.getStartDate()).size()));
        for (ALI activityLineInterval:activityLineIntervals) {
            if(!activity.getId().equals(activityLineInterval.getActivity().getId()) || shiftContainsALI(activityLineInterval,shift)){
                continue;
            }
            if(activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                possibleActivityLineIntervals.add(new ALIWrapper(activityLineInterval,shift));
            }
        }
        Collections.shuffle(possibleActivityLineIntervals);
        int[] randomRange= ShiftPlanningUtility.getRandomRange(possibleActivityLineIntervals.size(),workingRandom);
        possibleActivityLineIntervals=possibleActivityLineIntervals.subList(randomRange[0],randomRange[1]);
        ALIChangeMoveIterator ALIChangeMoveIterator = new ALIChangeMoveIterator(possibleActivityLineIntervals);
        return ALIChangeMoveIterator;
    }

    private boolean shiftContainsALI(ALI ali,Shift shift){
        for (ALI activityLineInterval : shift.getActivityLineIntervals()) {
            if(activityLineInterval.getStart().equals(ali.getStart()) && activityLineInterval.getEnd().equals(ali.getEnd())){
                return true;
            }
        }
        return false;
    }


}
