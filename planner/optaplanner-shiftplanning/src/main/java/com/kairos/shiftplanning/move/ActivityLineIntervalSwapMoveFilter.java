package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ActivityLineIntervalSwapMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,SwapMove> {
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalChangeMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, SwapMove selection) {
        ActivityLineInterval activityLineInterval1 = (ActivityLineInterval) selection.getLeftEntity();
        ActivityLineInterval activityLineInterval2 = (ActivityLineInterval) selection.getRightEntity();
        if(activityLineInterval1.getActivity().isTypeAbsence()!=activityLineInterval2.getActivity().isTypeAbsence()){
            return false;
        }
        ShiftImp shiftImp1 = activityLineInterval2.getShift();
        ShiftImp shiftImp2 = activityLineInterval1.getShift();
        if(Objects.equals(shiftImp1, shiftImp2)){
            return false;
        }
        if(activityLineInterval1.getActivity().isTypeAbsence()!=activityLineInterval2.getActivity().isTypeAbsence()){
            return false;
        }
        if(!activityLineInterval2.getStart().toLocalDate().equals(activityLineInterval1.getStart().toLocalDate())){
            return false;
        }

        if(shiftImp1 !=null && !shiftImp1.getDate().equals(activityLineInterval1.getStart().toLocalDate())){
            return false;
        }
        if(shiftImp2 !=null && !shiftImp2.getDate().equals(activityLineInterval2.getStart().toLocalDate())){
            return false;
        }
        //log.info("Acceptable swap move activity:"+selection);
        return true;
    }
}
