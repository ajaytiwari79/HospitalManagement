package com.kairos.shiftplanningNewVersion.move;


import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import com.kairos.shiftplanningNewVersion.move.helper.ALIWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ALIChangeMoveIterator<T>
        implements Iterator<ALIChangeMove> {
    private static Logger log= LoggerFactory.getLogger(ALIChangeMoveIterator.class);

    List<ALIWrapper> ALIWrappers;
    private Random workingRandom;
    int n=0;
    public ALIChangeMoveIterator(List<ALIWrapper> ALIWrappers, Random workingRandom) {
        this.ALIWrappers = ALIWrappers;
        this.workingRandom=workingRandom;
    }
    public ALIChangeMoveIterator(List<ALIWrapper> ALIWrappers) {
        this.ALIWrappers = ALIWrappers;
    }
    @Override
    public boolean hasNext() {
        return n< ALIWrappers.size();
    }
    @Override
    public ALIChangeMove next() {
        int index=workingRandom==null?n++:workingRandom.nextInt(ALIWrappers.size());
        ALIWrapper ALIWrapper = ALIWrappers.get(index);
        if(ALIWrapper.getActivityLineInterval().getActivity().isTypeAbsence()){
            log.debug("providing absence move");
        }
        List<ALI> activityLineIntervals = ALIWrapper.getShiftImp() != null ? ALIWrapper.getShiftImp().getActivityLineIntervals() : null;
        List<ALI> exIntervals = StaffingLevelPlanningUtility.getOverlappingActivityLineIntervals(activityLineIntervals, ALIWrapper.getActivityLineInterval());
        ALIChangeMove changeMove = new ALIChangeMove(ALIWrapper.getActivityLineInterval(), ALIWrapper.getShiftImp(),exIntervals,null);
        return changeMove;
    }
}

