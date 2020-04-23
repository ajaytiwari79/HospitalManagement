package com.kairos.shiftplanning.move;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

public class ShiftBreakMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,ChangeMove> {

    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, ChangeMove selection) {
        ShiftBreak shiftBreak= (ShiftBreak) selection.getEntity();
        ZonedDateTime planningTime=(ZonedDateTime) selection.getToPlanningValue();
        DateTimeInterval possibleBreakInterval= ShiftPlanningUtility.getPossibleBreakStartInterval(shiftBreak,shiftBreak.getShift());
        return ShiftPlanningUtility.intervalConstainsTimeIncludingEnd(possibleBreakInterval,planningTime);
    }
}
