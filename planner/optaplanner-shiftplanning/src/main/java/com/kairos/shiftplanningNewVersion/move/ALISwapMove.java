package com.kairos.shiftplanningNewVersion.move;




import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.move.helper.ALIChangeMoveHelper;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isNull;

public class ALISwapMove extends AbstractMove<StaffingLevelSolution> {
    private ALI leftActivityLineInterval;
    private Shift leftToShift;
    private List<ALI> leftExActivityLineIntervals;
    private Shift leftExToShift;
    private ALI rightActivityLineInterval;
    private Shift rightToShift;
    private List<ALI> rightExActivityLineIntervals;
    private Shift rightExToShift;
    private static Logger log= LoggerFactory.getLogger(ALISwapMove.class);
    public ALISwapMove(ALI leftActivityLineInterval, Shift leftToShift, List<ALI> leftExActivityLineIntervals, Shift leftExToShift,
                       ALI rightActivityLineInterval, Shift rightToShift, List<ALI> rightExActivityLineIntervals, Shift rightExToShift) {
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
    protected AbstractMove<StaffingLevelSolution> createUndoMove(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        return new ALISwapMove(leftActivityLineInterval, leftActivityLineInterval.getShift(), leftExActivityLineIntervals,rightActivityLineInterval.getShift(),
                rightActivityLineInterval, rightActivityLineInterval.getShift(), rightExActivityLineIntervals,leftActivityLineInterval.getShift());
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        try {
            ALIChangeMoveHelper.assignActivityIntervalToShift
                    (scoreDirector, leftActivityLineInterval, leftToShift, leftExActivityLineIntervals, leftExToShift);
            ALIChangeMoveHelper.assignActivityIntervalToShift
                    (scoreDirector, rightActivityLineInterval, rightToShift, rightExActivityLineIntervals, rightExToShift);
        }catch(Exception e){
            log.error("",e);
            throw e;
        }
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<StaffingLevelSolution> scoreDirector) {
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
    public ALISwapMove rebase(ScoreDirector<StaffingLevelSolution> destinationScoreDirector) {
        return new ALISwapMove(destinationScoreDirector.lookUpWorkingObject(leftActivityLineInterval),destinationScoreDirector.lookUpWorkingObject(leftToShift),rebaseList(leftExActivityLineIntervals,destinationScoreDirector),destinationScoreDirector.lookUpWorkingObject(leftExToShift),destinationScoreDirector.lookUpWorkingObject(rightActivityLineInterval),destinationScoreDirector.lookUpWorkingObject(rightToShift),rebaseList(rightExActivityLineIntervals,destinationScoreDirector),destinationScoreDirector.lookUpWorkingObject(rightExToShift));
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