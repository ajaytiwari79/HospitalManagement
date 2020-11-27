package com.kairos.shiftplanningNewVersion.filter;

import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Objects;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

public class ALISwapMoveFilter implements SelectionFilter<StaffingLevelSolution, SwapMove> {
    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, SwapMove selection) {
        ALI leftALI = (ALI) selection.getLeftEntity();
        ALI rightAli = (ALI) selection.getRightEntity();
        if(isNotNull(leftALI.getShift()) && isNotNull(rightAli.getShift()) && leftALI.getShift().getId().equals(rightAli.getShift().getStart())){
            return false;
        }
        if(isNotNull(leftALI.getShift()) && leftALI.getShift().getActivityLineIntervals().stream().anyMatch(ali1 -> ali1.getInterval().overlaps(rightAli.getInterval()))){
            return false;
        }
        if(isNotNull(rightAli.getShift()) && rightAli.getShift().getActivityLineIntervals().stream().anyMatch(ali1 -> ali1.getInterval().overlaps(leftALI.getInterval()))){
            return false;
        }
        return true;
    }
}
