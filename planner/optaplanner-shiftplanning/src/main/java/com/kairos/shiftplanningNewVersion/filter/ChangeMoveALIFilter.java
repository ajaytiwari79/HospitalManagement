package com.kairos.shiftplanningNewVersion.filter;

import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Objects;

public class ChangeMoveALIFilter implements SelectionFilter<StaffingLevelSolution, ChangeMove> {
    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, ChangeMove selection) {
        ALI ali = (ALI) selection.getEntity();
        Shift shift = (Shift) selection.getToPlanningValue();
        if(Objects.equals(ali.getShift(), shift)){
            return false;
        }
        if(shift !=null && shift.getActivityLineIntervals().stream().anyMatch(ali1 -> ali1.getInterval().overlaps(ali.getInterval()))){
            return false;
        }
        return true;
    }
}
