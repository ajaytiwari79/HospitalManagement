package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class  ActivityLineIntervalChangeMoveIterator<T>
        implements Iterator<ActivityLineIntervalChangeMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveIterator.class);

    List<ActivityLineIntervalWrapper> activityLineIntervalWrappers;
    private Random workingRandom;
    int n=0;
    public ActivityLineIntervalChangeMoveIterator(List<ActivityLineIntervalWrapper> activityLineIntervalWrappers,Random workingRandom) {
        this.activityLineIntervalWrappers = activityLineIntervalWrappers;
        this.workingRandom=workingRandom;
    }
    public ActivityLineIntervalChangeMoveIterator(List<ActivityLineIntervalWrapper> activityLineIntervalWrappers) {
        this.activityLineIntervalWrappers = activityLineIntervalWrappers;
    }
    @Override
    public boolean hasNext() {
        return n<activityLineIntervalWrappers.size();
    }
    @Override
    public ActivityLineIntervalChangeMove next() {
        int index=workingRandom==null?n++:workingRandom.nextInt(activityLineIntervalWrappers.size());
        ActivityLineIntervalWrapper activityLineIntervalWrapper= activityLineIntervalWrappers.get(index);
        if(activityLineIntervalWrapper.getActivityLineInterval().getActivity().isTypeAbsence()){
            log.debug("providing absence move");
        }
        List<ActivityLineInterval> exIntervals = ShiftPlanningUtility.getOverlappingActivityLineIntervals(activityLineIntervalWrapper.getShiftImp()==null?null:activityLineIntervalWrapper.getShiftImp().getActivityLineIntervals(),activityLineIntervalWrapper.getActivityLineInterval());
        ActivityLineIntervalChangeMove changeMove = new ActivityLineIntervalChangeMove(activityLineIntervalWrapper.getActivityLineInterval(),activityLineIntervalWrapper.getShiftImp(),exIntervals,null);
        return changeMove;
    }
}
/*public class  ActivityLineIntervalChangeMoveIterator<T>
        implements Iterator<ActivityLineIntervalIteratorMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveIterator.class);

    List<ActivityLineIntervalWrapper> activityLineInterval;
    private Random workingRandom;

    public ActivityLineIntervalChangeMoveIterator(List<ActivityLineIntervalWrapper> activityLineInterval,Random workingRandom) {
        this.activityLineInterval = activityLineInterval;
        this.workingRandom=workingRandom;
    }

    @Override
    public boolean hasNext() {
        return true;
    }
    @Override
    public ActivityLineIntervalIteratorMove next() {
        n++;
        int index=workingRandom.nextInt(activityLineInterval.size());
        log.info(this.toString());
        ActivityLineIntervalWrapper activityLineIntervalWrapper= activityLineInterval.get(index);
        ActivityLineIntervalIteratorMove changeMove = new ActivityLineIntervalIteratorMove(activityLineIntervalWrapper.getActivityLineInterval(),activityLineIntervalWrapper.getShiftImp());
        return changeMove;
    }
}*/
