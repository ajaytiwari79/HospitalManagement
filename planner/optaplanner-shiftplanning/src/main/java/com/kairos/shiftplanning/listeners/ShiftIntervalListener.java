package com.kairos.shiftplanning.listeners;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShiftIntervalListener implements VariableListener<ActivityLineInterval>{
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    private static Logger log= LoggerFactory.getLogger(ShiftIntervalListener.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        //Not in use
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        updateShiftTimeFromScoreDirector(activityLineInterval.getShift(),activityLineInterval, scoreDirector);
    }

    private void updateShiftTimeFromScoreDirector(ShiftImp shift, ActivityLineInterval activityLineInterval, ScoreDirector scoreDirector) {
        if(shift!=null){
            //List<ActivityLineInterval> activityLineIntervals=((ShiftRequestPhasePlanningSolution)scoreDirector.getWorkingSolution()).getActivityLineIntervalsList().stream()
              //      .filter(ali->shift.equals(ali.getShift())).collect(Collectors.toList());
            List<ActivityLineInterval> activityLineIntervals=shift.getActivityLineIntervals();
            DateTime[] startAndEnd= getEarliestStartAndLatestEnd(activityLineIntervals);
            //activityLineIntervals.sort(Comparator.comparing(ActivityLineInterval::getStart));
            scoreDirector.beforeVariableChanged(shift, START_TIME);
            //shift.setStartTime(activityLineIntervals.get(0).getStart().toLocalTime());
            shift.setStartTime(startAndEnd[0]==null?null:startAndEnd[0].toLocalTime());
            scoreDirector.afterVariableChanged(shift, START_TIME);


            scoreDirector.beforeVariableChanged(shift, END_TIME);
            //shift.setEndTime(activityLineIntervals.get(activityLineIntervals.size()-1).getEnd().toLocalTime());
            shift.setEndTime(startAndEnd[1]==null?null:startAndEnd[1].toLocalTime());
            scoreDirector.afterVariableChanged(shift, END_TIME);
           // log.info("making shifts mins"+shift.getMinutes());
        }
    }
    public DateTime[] getEarliestStartAndLatestEnd(List<ActivityLineInterval> activityLineIntervals){
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

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        //Not in use
    }
    @Deprecated
    private void updateShiftTime(ShiftImp shift, ActivityLineInterval activityLineInterval, ScoreDirector scoreDirector) {
        if(shift!=null){
            if(shift.getStartTime()==null ||
                    shift.getStartTime().isAfter(activityLineInterval.getStart().toLocalTime())){
                scoreDirector.beforeVariableChanged(shift, START_TIME);
                shift.setStartTime(activityLineInterval.getStart().toLocalTime());
                scoreDirector.afterVariableChanged(shift, START_TIME);
            }
            if(shift.getEndTime()==null ||
                    shift.getEndTime().isBefore(activityLineInterval.getEnd().toLocalTime())){
                scoreDirector.beforeVariableChanged(shift, END_TIME);
                shift.setEndTime(activityLineInterval.getEnd().toLocalTime());
                scoreDirector.afterVariableChanged(shift, END_TIME);
            }
            log.info("making shifts mins"+shift.getMinutes());
        }

    }
}
