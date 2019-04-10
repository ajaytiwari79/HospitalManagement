package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ActivityLineIntervalChangeMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,ChangeMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ChangeMove selection) {
        ActivityLineInterval activityLineInterval = (ActivityLineInterval) selection.getEntity();
        DateTime dt=null;
        ShiftImp shiftImp = (ShiftImp) selection.getToPlanningValue();
        /*if((dt=activityLineInterval.getStart()).getDayOfMonth()==12 && dt.getMinuteOfHour()==15 && dt.getHourOfDay()==15 && activityLineInterval.getShift()!=null &&  shiftImp==null){
            log.info("MAKING MOVE :"+selection);
        }*/

        //check if shift already contains that activity
        //TODO we're limiting moves here BIGFIXME
        /*if(shiftImp!=null && shiftImp.getActivityLineIntervalsList()!=null) {
            for (ActivityLineInterval activityLineInterval1 : shiftImp.getActivityLineIntervalsList()) {
                if (activityLineInterval.compareTo(activityLineInterval1) == 0) {
                    return false;
                }
            }
        }*/
        if(Objects.equals(activityLineInterval.getShift(), shiftImp)){
            return false;
        }
        if(shiftImp !=null && !shiftImp.getDate().equals(activityLineInterval.getStart().toLocalDate())){
            return false;
        }
        //log.info("Acceptable change move activity:"+selection);
        return true;
    }
}
