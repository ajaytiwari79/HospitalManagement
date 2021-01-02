package com.kairos.shiftplanningNewVersion.move;

import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class ALIChangeMoveFilter implements SelectionFilter<StaffingLevelSolution,ChangeMove> {
    private static Logger log= LoggerFactory.getLogger(ALIChangeMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, ChangeMove selection) {
        ALI activityLineInterval = (ALI) selection.getEntity();
        DateTime dt=null;
        Shift shiftImp = (Shift) selection.getToPlanningValue();
        if(Objects.equals(activityLineInterval.getShift(), shiftImp)){
            return false;
        }
        if(shiftImp !=null && !shiftImp.getStartDate().equals(activityLineInterval.getStart().toLocalDate())){
            return false;
        }
        return true;
    }
}
