package com.kairos.shiftplanningNewVersion.listeners;

import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.utils.BreakUtils;
import com.kairos.shiftplanningNewVersion.utils.PlannedTimeTypeUtils;
import com.kairos.shiftplanningNewVersion.utils.StaffingLevelPlanningUtility;
import com.kairos.shiftplanningNewVersion.utils.UpdateTimeAndPayoutDetails;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


public class ShiftTimeListener implements VariableListener<Shift> {
    public static final String START_TIME = "startTime";

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Shift shift) {
        updateShiftStartAndEndTimes(scoreDirector, shift);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }
    private void updateShiftStartAndEndTimes(ScoreDirector scoreDirector, Shift shift){
        if(shift.getActivityLineIntervals().isEmpty()){
            scoreDirector.beforeVariableChanged(shift, START_TIME);
            shift.setStartTime(null);
            shift.setShiftActivities(new ArrayList<>());
            shift.setActivityIds(new HashSet<>());
            shift.setBreakActivities(new ArrayList<>());
            shift.setActivitiesPlannedTimeIds(new HashSet<>());
            shift.setActivitiesTimeTypeIds(new HashSet<>());
            scoreDirector.afterVariableChanged(shift, START_TIME);
            shift.setEndTime(null);
            return;
        }
        ZonedDateTime[] startAndEnd=getEarliestStartAndLatestEnd(shift.getActivityLineIntervals());
        scoreDirector.beforeVariableChanged(shift, START_TIME);
        shift.setStartTime(startAndEnd[0].toLocalTime());
        scoreDirector.afterVariableChanged(shift, START_TIME);
        shift.setEndTime(startAndEnd[1].toLocalTime());
        Object[] objects = StaffingLevelPlanningUtility.getMergedShiftActivitys(shift);
        //PlannedTimeTypeUtils.addPlannedTimeInShift(shift);
        //BreakUtils.updateBreakInShift(shift);
        //System.out.println("dasdasd");
        //UpdateTimeAndPayoutDetails.updateTimeBankAndPayoutDetails(shift);
        shift.setShiftActivities((List<ShiftActivity>) objects[0]);
        shift.setActivityIds((Set<BigInteger>) objects[1]);
        shift.setActivitiesTimeTypeIds((Set<BigInteger>) objects[3]);
    }
    private ZonedDateTime[] getEarliestStartAndLatestEnd(List<ALI> activityLineIntervals){
        ZonedDateTime[] startAndEnd= new ZonedDateTime[2];
        for (ALI activityLineInterval:activityLineIntervals) {
            if(startAndEnd[0]==null && startAndEnd[1]==null){
                startAndEnd[0]=activityLineInterval.getStart();
                startAndEnd[1]=activityLineInterval.getEnd();
                continue;
            }else{
                startAndEnd[0]=activityLineInterval.getStart().isBefore(startAndEnd[0])?activityLineInterval.getStart():startAndEnd[0];
                startAndEnd[1]=activityLineInterval.getEnd().isAfter(startAndEnd[1])?activityLineInterval.getEnd():startAndEnd[1];
            }

        }

        return startAndEnd;
    }
}
