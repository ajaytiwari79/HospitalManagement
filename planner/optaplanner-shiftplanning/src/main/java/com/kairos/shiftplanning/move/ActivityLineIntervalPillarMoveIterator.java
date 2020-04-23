package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ActivityLineIntervalPillarMoveIterator<T>
        implements Iterator<ActivityLineIntervalPillarMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalPillarMoveIterator.class);

    List<List<ActivityLineIntervalWrapper>> activityLineIntervalWrappersList;
    private Random workingRandom;
    int n=0;
    public ActivityLineIntervalPillarMoveIterator(List<List<ActivityLineIntervalWrapper>> activityLineIntervalWrappersList, Random workingRandom) {
        this.activityLineIntervalWrappersList = activityLineIntervalWrappersList;
        this.workingRandom=workingRandom;
    }
    @Override
    public boolean hasNext() {
        return n<activityLineIntervalWrappersList.size();
    }
    @Override
    public ActivityLineIntervalPillarMove next() {
       int index=workingRandom==null?n:workingRandom.nextInt(activityLineIntervalWrappersList.size());
       n++;
        List<ActivityLineIntervalWrapper> activityLineIntervalWrappers= activityLineIntervalWrappersList.get(index);
        List<ActivityLineIntervalWrapper> exActivityLineIntervalWrappersThisShift= ShiftPlanningUtility.buildNullAssignWrappersForExIntervals(activityLineIntervalWrappers);
        ActivityLineIntervalPillarMove pillarMove = new ActivityLineIntervalPillarMove(activityLineIntervalWrappers,exActivityLineIntervalWrappersThisShift);
        log.debug("providing move ::::::::::::::{}",pillarMove);
        return pillarMove;
    }
}
