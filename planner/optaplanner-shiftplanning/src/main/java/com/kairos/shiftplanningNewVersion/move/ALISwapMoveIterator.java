package com.kairos.shiftplanningNewVersion.move;


import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ALISwapMoveIterator<T>
        implements Iterator<ALISwapMove> {
    private static Logger log= LoggerFactory.getLogger(ALISwapMoveIterator.class);

    List<ALI> activityLineInterval;
    private Random workingRandom;
    int n=0;
    public ALISwapMoveIterator(List<ALI> activityLineInterval, Random workingRandom) {
        this.activityLineInterval = activityLineInterval;
        this.workingRandom=workingRandom;
    }
    public ALISwapMoveIterator(List<ALI> activityLineInterval) {
        this.activityLineInterval = activityLineInterval;
    }
    @Override
    public boolean hasNext() {
        return n< activityLineInterval.size();
    }
    @Override
    public ALISwapMove next() {
        n++;
        ALI leftActivityLineInterval= activityLineInterval.get(workingRandom.nextInt(activityLineInterval.size()));
        ALI rightActivityLineInterval= activityLineInterval.get(workingRandom.nextInt(activityLineInterval.size()));
        boolean sameInterval=leftActivityLineInterval.getStart().isEqual(rightActivityLineInterval.getStart());

        List<ALI> leftExIntervals =sameInterval || rightActivityLineInterval.getShift()==null?null: StaffingLevelPlanningUtility.getOverlappingActivityLineIntervals(rightActivityLineInterval.getShift().getActivityLineIntervals(),leftActivityLineInterval);
        List<ALI> rightExIntervals =sameInterval||leftActivityLineInterval.getShift()==null?null: StaffingLevelPlanningUtility.getOverlappingActivityLineIntervals(leftActivityLineInterval.getShift().getActivityLineIntervals(),rightActivityLineInterval);
        ALISwapMove swapMove=new ALISwapMove(leftActivityLineInterval, rightActivityLineInterval.getShift(), leftExIntervals,null,
                rightActivityLineInterval, leftActivityLineInterval.getShift(), rightExIntervals,null);
        return swapMove;
    }
}
