package com.kairos.shiftplanning.listeners;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShiftStartTimeListener implements VariableListener<ShiftImp> {
    private static Logger log= LoggerFactory.getLogger(ShiftStartTimeListener.class);

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
        //log.info("{} intervals [{}]",shiftImp.getPrettyId(),shiftImp.getActivityLineIntervalsList());
        if(shiftImp.getActivityLineIntervals().isEmpty()){
            scoreDirector.beforeVariableChanged(shiftImp,"startTime");
            shiftImp.setStartTime(null);
            scoreDirector.afterVariableChanged(shiftImp,"startTime");
            shiftImp.setEndTime(null);
            return;
        }
        DateTime[] startAndEnd=getEarliestStartAndLatestEnd(shiftImp.getActivityLineIntervals());
        scoreDirector.beforeVariableChanged(shiftImp,"startTime");
        shiftImp.setStartTime(startAndEnd[0].toLocalTime());
        scoreDirector.afterVariableChanged(shiftImp,"startTime");
        shiftImp.setEndTime(startAndEnd[1].toLocalTime());
        /*if(new Interval(startAndEnd[0],startAndEnd[1]).toDuration().toStandardMinutes().getMinutes()!=ShiftPlanningUtility.getMinutesFromIntervals(shiftImp.getActivityLineIntervalsList())){
            log.info("problematic");
        }*/
        //log.info("{} setting start and end as: {}--{}",shiftImp.getPrettyId(),startAndEnd[0],startAndEnd[1]);
        /*if(shiftImp!=null && shiftImp.getActivityLineIntervalsList().size()==1 && shiftImp.getInterval().toDuration().getStandardMinutes()!=15l){
            log.info("+++++++++++++++++++++++++"+ ShiftPlanningUtility.getIntervalAsString(shiftImp.getInterval()));
        }*/

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
