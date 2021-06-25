package com.kairos.shiftplanningNewVersion.move;

import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndirectActivityMoveFilter implements SelectionFilter<StaffingLevelSolution,ChangeMove> {
    private static Logger log= LoggerFactory.getLogger(IndirectActivityMoveFilter.class);

    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, ChangeMove selection) {
        IndirectActivity indirectActivity= (IndirectActivity) selection.getEntity();
        DateTime planningTime=(DateTime) selection.getToPlanningValue();
        return true;
    }
}
