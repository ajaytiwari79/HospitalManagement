package com.kairos.shiftplanningNewVersion.move;




import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ALISwapMoveFilter implements SelectionFilter<StaffingLevelSolution,SwapMove> {
    private static Logger log= LoggerFactory.getLogger(ALIChangeMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, SwapMove selection) {
        ALI activityLineInterval1 = (ALI) selection.getLeftEntity();
        ALI activityLineInterval2 = (ALI) selection.getRightEntity();
        if(activityLineInterval1.getActivity().isTypeAbsence()!=activityLineInterval2.getActivity().isTypeAbsence()){
            return false;
        }
        Shift shiftImp1 = activityLineInterval2.getShift();
        Shift shiftImp2 = activityLineInterval1.getShift();
        if(Objects.equals(shiftImp1, shiftImp2)){
            return false;
        }
        if(activityLineInterval1.getActivity().isTypeAbsence()!=activityLineInterval2.getActivity().isTypeAbsence()){
            return false;
        }
        if(!activityLineInterval2.getStart().toLocalDate().equals(activityLineInterval1.getStart().toLocalDate())){
            return false;
        }

        if(shiftImp1 !=null && !shiftImp1.getStartDate().equals(activityLineInterval1.getStart().toLocalDate())){
            return false;
        }
        if(shiftImp2 !=null && !shiftImp2.getStartDate().equals(activityLineInterval2.getStart().toLocalDate())){
            return false;
        }
        return true;
    }
}
