package com.kairos.shiftplanning.utils;

import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class MoveableEntitySelectionFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution, ShiftImp> {

    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ShiftImp shift) {
        return shift!=null?!shift.isLocked():true;
    }


}
