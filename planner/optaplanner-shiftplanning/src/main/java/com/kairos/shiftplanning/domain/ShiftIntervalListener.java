package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ShiftIntervalListener implements VariableListener<ActivityLineInterval>{
    private static Logger log= LoggerFactory.getLogger(ShiftIntervalListener.class);

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {
        updateShiftTimeFromScoreDirector(activityLineInterval.getShift(),activityLineInterval, scoreDirector);
    }

    private void updateShiftTimeFromScoreDirector(ShiftRequestPhase shift, ActivityLineInterval activityLineInterval, ScoreDirector scoreDirector) {
        if(shift!=null){
            //List<ActivityLineInterval> activityLineIntervals=((ShiftRequestPhasePlanningSolution)scoreDirector.getWorkingSolution()).getActivityLineIntervalsList().stream()
              //      .filter(ali->shift.equals(ali.getShift())).collect(Collectors.toList());
            List<ActivityLineInterval> activityLineIntervals=shift.getActivityLineIntervals();
            DateTime[] startAndEnd= getEarliestStartAndLatestEnd(activityLineIntervals);
            //activityLineIntervals.sort(Comparator.comparing(ActivityLineInterval::getStart));
            scoreDirector.beforeVariableChanged(shift,"startTime");
            //shift.setStartTime(activityLineIntervals.get(0).getStart().toLocalTime());
            shift.setStartTime(startAndEnd[0]==null?null:startAndEnd[0].toLocalTime());
            scoreDirector.afterVariableChanged(shift,"startTime");


            scoreDirector.beforeVariableChanged(shift,"endTime");
            //shift.setEndTime(activityLineIntervals.get(activityLineIntervals.size()-1).getEnd().toLocalTime());
            shift.setEndTime(startAndEnd[1]==null?null:startAndEnd[1].toLocalTime());
            scoreDirector.afterVariableChanged(shift,"endTime");
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

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ActivityLineInterval activityLineInterval) {

    }
    @Deprecated
    private void updateShiftTime(ShiftRequestPhase shift, ActivityLineInterval activityLineInterval,ScoreDirector scoreDirector) {
        if(shift!=null){
            if(shift.getStartTime()==null ||
                    shift.getStartTime().isAfter(activityLineInterval.getStart().toLocalTime())){
                scoreDirector.beforeVariableChanged(shift,"startTime");
                shift.setStartTime(activityLineInterval.getStart().toLocalTime());
                scoreDirector.afterVariableChanged(shift,"startTime");
            }
            if(shift.getEndTime()==null ||
                    shift.getEndTime().isBefore(activityLineInterval.getEnd().toLocalTime())){
                scoreDirector.beforeVariableChanged(shift,"endTime");
                shift.setEndTime(activityLineInterval.getEnd().toLocalTime());
                scoreDirector.afterVariableChanged(shift,"endTime");
            }
            log.info("making shifts mins"+shift.getMinutes());
        }

    }
}
