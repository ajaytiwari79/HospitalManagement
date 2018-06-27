package com.kairos.shiftplanning.domain.wta.listeners;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.move.helper.ActivityLineIntervalChangeMoveHelper;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShiftStartTimeListener implements VariableListener<ShiftRequestPhase> {
    private static Logger log= LoggerFactory.getLogger(ShiftStartTimeListener.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {
        updateShiftStartAndEndTimes(scoreDirector,shiftRequestPhase);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ShiftRequestPhase shiftRequestPhase) {

    }
    private void updateShiftStartAndEndTimes(ScoreDirector scoreDirector,ShiftRequestPhase shiftRequestPhase){
        //log.info("{} intervals [{}]",shiftRequestPhase.getPrettyId(),shiftRequestPhase.getActivityLineIntervalsList());
        if(shiftRequestPhase.getActivityLineIntervals().isEmpty()){
            scoreDirector.beforeVariableChanged(shiftRequestPhase,"startTime");
            shiftRequestPhase.setStartTime(null);
            scoreDirector.afterVariableChanged(shiftRequestPhase,"startTime");
            shiftRequestPhase.setEndTime(null);
            return;
        }
        DateTime[] startAndEnd=getEarliestStartAndLatestEnd(shiftRequestPhase.getActivityLineIntervals());
        scoreDirector.beforeVariableChanged(shiftRequestPhase,"startTime");
        shiftRequestPhase.setStartTime(startAndEnd[0].toLocalTime());
        scoreDirector.afterVariableChanged(shiftRequestPhase,"startTime");
        shiftRequestPhase.setEndTime(startAndEnd[1].toLocalTime());
        /*if(new Interval(startAndEnd[0],startAndEnd[1]).toDuration().toStandardMinutes().getMinutes()!=ShiftPlanningUtility.getMinutesFromIntervals(shiftRequestPhase.getActivityLineIntervalsList())){
            log.info("problematic");
        }*/
        //log.info("{} setting start and end as: {}--{}",shiftRequestPhase.getPrettyId(),startAndEnd[0],startAndEnd[1]);
        /*if(shiftRequestPhase!=null && shiftRequestPhase.getActivityLineIntervalsList().size()==1 && shiftRequestPhase.getInterval().toDuration().getStandardMinutes()!=15l){
            log.info("+++++++++++++++++++++++++"+ ShiftPlanningUtility.getIntervalAsString(shiftRequestPhase.getInterval()));
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
