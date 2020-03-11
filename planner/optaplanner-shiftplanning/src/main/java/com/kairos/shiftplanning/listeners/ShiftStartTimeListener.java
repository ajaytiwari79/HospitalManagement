package com.kairos.shiftplanning.listeners;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.List;

public class ShiftStartTimeListener implements VariableListener<ShiftImp> {
    public static final String START_TIME = "startTime";

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        //Not in use
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        updateShiftStartAndEndTimes(scoreDirector, shiftImp);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ShiftImp shiftImp) {
        //Not in use
    }
    private void updateShiftStartAndEndTimes(ScoreDirector scoreDirector, ShiftImp shiftImp){
        if(shiftImp.getActivityLineIntervals().isEmpty()){
            scoreDirector.beforeVariableChanged(shiftImp, START_TIME);
            shiftImp.setStartTime(null);
            shiftImp.setShiftActivities(new ArrayList<>());
            scoreDirector.afterVariableChanged(shiftImp, START_TIME);
            shiftImp.setEndTime(null);
            return;
        }
        shiftImp.setShiftActivities(ShiftPlanningUtility.getMergedShiftActivitys(shiftImp.getActivityLineIntervals()));
        DateTime[] startAndEnd=getEarliestStartAndLatestEnd(shiftImp.getActivityLineIntervals());
        scoreDirector.beforeVariableChanged(shiftImp, START_TIME);
        shiftImp.setStartTime(startAndEnd[0].toLocalTime());
        scoreDirector.afterVariableChanged(shiftImp, START_TIME);
        shiftImp.setEndTime(startAndEnd[1].toLocalTime());

    }
    private DateTime[] getEarliestStartAndLatestEnd(List<ActivityLineInterval> activityLineIntervals){
        DateTime[] startAndEnd= new DateTime[2];
        for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
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
