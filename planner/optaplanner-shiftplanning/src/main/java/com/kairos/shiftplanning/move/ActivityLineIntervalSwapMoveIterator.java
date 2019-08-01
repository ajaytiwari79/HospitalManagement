package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ActivityLineIntervalSwapMoveIterator<T>
        implements Iterator<ActivityLineIntervalSwapMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalSwapMoveIterator.class);

    List<ActivityLineInterval> activityLineInterval;
    private Random workingRandom;
    int n=0;
    public ActivityLineIntervalSwapMoveIterator(List<ActivityLineInterval> activityLineInterval, Random workingRandom) {
        this.activityLineInterval = activityLineInterval;
        this.workingRandom=workingRandom;
    }
    public ActivityLineIntervalSwapMoveIterator(List<ActivityLineInterval> activityLineInterval) {
        this.activityLineInterval = activityLineInterval;
    }
    @Override
    public boolean hasNext() {
        return n< activityLineInterval.size();
    }
    @Override
    public ActivityLineIntervalSwapMove next() {
       // int index=workingRandom==null?n++:workingRandom.nextInt(activityLineInterval.size());
        n++;
        ActivityLineInterval leftActivityLineInterval= activityLineInterval.get(workingRandom.nextInt(activityLineInterval.size()));
        ActivityLineInterval rightActivityLineInterval= activityLineInterval.get(workingRandom.nextInt(activityLineInterval.size()));
        boolean sameInterval=leftActivityLineInterval.getStart().isEqual(rightActivityLineInterval.getStart());

        List<ActivityLineInterval> leftExIntervals =sameInterval || rightActivityLineInterval.getShift()==null?null: ShiftPlanningUtility.getOverlappingActivityLineIntervals(rightActivityLineInterval.getShift().getActivityLineIntervals(),leftActivityLineInterval);
        List<ActivityLineInterval> rightExIntervals =sameInterval||leftActivityLineInterval.getShift()==null?null: ShiftPlanningUtility.getOverlappingActivityLineIntervals(leftActivityLineInterval.getShift().getActivityLineIntervals(),rightActivityLineInterval);
        ActivityLineIntervalSwapMove swapMove=new ActivityLineIntervalSwapMove(leftActivityLineInterval, rightActivityLineInterval.getShift(), leftExIntervals,null,
                rightActivityLineInterval, leftActivityLineInterval.getShift(), rightExIntervals,null);
        return swapMove;
    }
}
