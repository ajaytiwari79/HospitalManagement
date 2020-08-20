package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalChangeMoveHelper;
import com.kairos.shiftplanning.solution.ShiftPlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;

public class ActivityLineIntervalSwapMove extends AbstractMove<ShiftPlanningSolution> {
    private ActivityLineInterval leftActivityLineInterval;
    private ShiftImp leftToShift;
    private List<ActivityLineInterval> leftExActivityLineIntervals;
    private ShiftImp leftExToShift;
    private ActivityLineInterval rightActivityLineInterval;
    private ShiftImp rightToShift;
    private List<ActivityLineInterval> rightExActivityLineIntervals;
    private ShiftImp rightExToShift;
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalSwapMove.class);
    public ActivityLineIntervalSwapMove(ActivityLineInterval leftActivityLineInterval, ShiftImp leftToShift, List<ActivityLineInterval> leftExActivityLineIntervals, ShiftImp leftExToShift,
                                        ActivityLineInterval rightActivityLineInterval, ShiftImp rightToShift, List<ActivityLineInterval> rightExActivityLineIntervals, ShiftImp rightExToShift) {
        this.leftActivityLineInterval = leftActivityLineInterval;
        this.leftToShift = leftToShift;
        this.rightActivityLineInterval = rightActivityLineInterval;
        this.leftExActivityLineIntervals=leftExActivityLineIntervals;
        this.leftExToShift=leftExToShift;
        this.rightToShift = rightToShift;
        this.rightExActivityLineIntervals=rightExActivityLineIntervals;
        this.rightExToShift=rightExToShift;
    }
    @Override
    protected AbstractMove<ShiftPlanningSolution> createUndoMove(ScoreDirector<ShiftPlanningSolution> scoreDirector) {
        return new ActivityLineIntervalSwapMove(leftActivityLineInterval, leftActivityLineInterval.getShift(), leftExActivityLineIntervals,rightActivityLineInterval.getShift(),
                rightActivityLineInterval, rightActivityLineInterval.getShift(), rightExActivityLineIntervals,leftActivityLineInterval.getShift());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<ShiftPlanningSolution> scoreDirector) {
        try {
            ActivityLineIntervalChangeMoveHelper.assignActivityIntervalToShift
                    (scoreDirector, leftActivityLineInterval, leftToShift, leftExActivityLineIntervals, leftExToShift);
            ActivityLineIntervalChangeMoveHelper.assignActivityIntervalToShift
                    (scoreDirector, rightActivityLineInterval, rightToShift, rightExActivityLineIntervals, rightExToShift);
        }catch(Exception e){
            log.error("",e);
            throw e;
        }
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<ShiftPlanningSolution> scoreDirector) {
        boolean isDoable=leftActivityLineInterval.getActivity().isTypeAbsence()==rightActivityLineInterval.getActivity().isTypeAbsence() &&
                !Objects.equals(leftActivityLineInterval,rightActivityLineInterval) &&
                !Objects.equals(leftActivityLineInterval.getShift(),rightActivityLineInterval.getShift());
        return isDoable;
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return Arrays.asList(leftActivityLineInterval, rightActivityLineInterval);
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Arrays.asList(leftActivityLineInterval.getShift(),rightActivityLineInterval.getShift());
    }

    @Override
    public ActivityLineIntervalSwapMove rebase(ScoreDirector<ShiftPlanningSolution> destinationScoreDirector) {
        return new ActivityLineIntervalSwapMove(destinationScoreDirector.lookUpWorkingObject(leftActivityLineInterval),destinationScoreDirector.lookUpWorkingObject(leftToShift),rebaseList(leftExActivityLineIntervals,destinationScoreDirector),destinationScoreDirector.lookUpWorkingObject(leftExToShift),destinationScoreDirector.lookUpWorkingObject(rightActivityLineInterval),destinationScoreDirector.lookUpWorkingObject(rightToShift),rebaseList(rightExActivityLineIntervals,destinationScoreDirector),destinationScoreDirector.lookUpWorkingObject(rightExToShift));
    }

    protected static  <E> List<E> rebaseList(List<E> externalObjectList, ScoreDirector<?> destinationScoreDirector) {
        if(isNull(externalObjectList)){
            return null;
        }
        List<E> rebasedObjectList = new ArrayList<>(externalObjectList.size());
        for (E entity : externalObjectList) {
            rebasedObjectList.add(destinationScoreDirector.lookUpWorkingObject(entity));
        }
        return rebasedObjectList;
    }

    @Override
    public String toString() {
        return "ActivityLineIntervalSwapMove"+"[" +//+(i%2==0?"DO":"UNDO")+
                "{{leftActivityLineInterval=" + leftActivityLineInterval +"{"+leftActivityLineInterval.getShift()+"->"+ rightActivityLineInterval.getShift()+"}}"+
                (leftExActivityLineIntervals!=null?("(leftExActivityLineIntervals" + leftExActivityLineIntervals.stream().map(a->a.getLabel()).collect(Collectors.toList())+"->"+leftExToShift+")}"):"")+
                "{{rightActivityLineInterval=" + rightActivityLineInterval +"{"+rightActivityLineInterval.getShift()+"->"+ leftActivityLineInterval.getShift()+"}}"+
                (rightExActivityLineIntervals!=null?("(rightExActivityLineIntervals" + rightExActivityLineIntervals.stream().map(a->a.getLabel()).collect(Collectors.toList())+"->"+rightExToShift+")}"):"")+
                ']';
    }
}