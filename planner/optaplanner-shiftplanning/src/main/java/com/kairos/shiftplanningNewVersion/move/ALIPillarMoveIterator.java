package com.kairos.shiftplanningNewVersion.move;

import com.kairos.shiftplanningNewVersion.move.helper.ALIWrapper;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ALIPillarMoveIterator<T>
        implements Iterator<ALIPillarMove> {
    private static Logger log= LoggerFactory.getLogger(ALIPillarMoveIterator.class);

    List<List<ALIWrapper>> activityLineIntervalWrappersList;
    private Random workingRandom;
    int n=0;
    public ALIPillarMoveIterator(List<List<ALIWrapper>> activityLineIntervalWrappersList, Random workingRandom) {
        this.activityLineIntervalWrappersList = activityLineIntervalWrappersList;
        this.workingRandom=workingRandom;
    }
    @Override
    public boolean hasNext() {
        return n<activityLineIntervalWrappersList.size();
    }
    @Override
    public ALIPillarMove next() {
       int index=workingRandom==null?n:workingRandom.nextInt(activityLineIntervalWrappersList.size());
       n++;
        List<ALIWrapper> ALIWrappers = activityLineIntervalWrappersList.get(index);
        List<ALIWrapper> exALIWrappersThisShift = StaffingLevelPlanningUtility.buildNullAssignWrappersForExIntervals(ALIWrappers);
        ALIPillarMove pillarMove = new ALIPillarMove(ALIWrappers, exALIWrappersThisShift);
        log.debug("providing move ::::::::::::::{}",pillarMove);
        return pillarMove;
    }
}
