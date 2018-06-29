package com.kairos.shiftplanning.domain.move;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.Objects;

public class ActivityLineIntervalChangeMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,ChangeMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ChangeMove selection) {
        ActivityLineInterval activityLineInterval = (ActivityLineInterval) selection.getEntity();
        DateTime dt=null;
        ShiftRequestPhase shiftRequestPhase= (ShiftRequestPhase) selection.getToPlanningValue();
        /*if((dt=activityLineInterval.getStart()).getDayOfMonth()==12 && dt.getMinuteOfHour()==15 && dt.getHourOfDay()==15 && activityLineInterval.getShift()!=null &&  shiftRequestPhase==null){
            log.info("MAKING MOVE :"+selection);
        }*/

        //check if shift already contains that activity
        //TODO we're limiting moves here BIGFIXME
        /*if(shiftRequestPhase!=null && shiftRequestPhase.getActivityLineIntervalsList()!=null) {
            for (ActivityLineInterval activityLineInterval1 : shiftRequestPhase.getActivityLineIntervalsList()) {
                if (activityLineInterval.compareTo(activityLineInterval1) == 0) {
                    return false;
                }
            }
        }*/
        if(Objects.equals(activityLineInterval.getShift(),shiftRequestPhase)){
            return false;
        }
        if(shiftRequestPhase!=null && !shiftRequestPhase.getDate().equals(activityLineInterval.getStart().toLocalDate())){
            return false;
        }
        //log.info("Acceptable change move activity:"+selection);
        return true;
    }
}
