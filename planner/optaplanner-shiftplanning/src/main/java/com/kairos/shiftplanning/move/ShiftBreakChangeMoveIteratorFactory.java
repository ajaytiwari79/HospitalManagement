package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.executioner.ShiftPlanningGenerator;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.joda.time.*;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveIteratorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * We'll be creating it just like MoveFactory as there are very less number of moves
 * but we're taking IteratorFactory in case we need to limit moves randomly using limited moves iterator.
 */
public class ShiftBreakChangeMoveIteratorFactory  implements MoveIteratorFactory<BreaksIndirectAndActivityPlanningSolution> {
    int n=0;
    private static Logger log= LoggerFactory.getLogger(ShiftPlanningGenerator.class);

    @Override
    public long getSize(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        BreaksIndirectAndActivityPlanningSolution workingSolution = scoreDirector.getWorkingSolution();
        return workingSolution.getShiftBreaks().size() * workingSolution.getPossibleStartDateTimes().size();
    }
    @Override
    public Iterator<? extends Move<BreaksIndirectAndActivityPlanningSolution>> createOriginalMoveIterator(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector) {
        return null;
    }
    @Override
    public Iterator<? extends Move<BreaksIndirectAndActivityPlanningSolution>> createRandomMoveIterator(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector, Random workingRandom) {
        //boolean inMem=ShiftPlanningUtility.checkDroolsMemory(scoreDirector);
        //log.info("Matrix in working memory: "+inMem);
        BreaksIndirectAndActivityPlanningSolution workingSolution = scoreDirector.getWorkingSolution();
        LocalDate date=workingSolution.getWeekDates().get(workingRandom.nextInt(workingSolution.getWeekDates().size()));
        List<ShiftBreakChangeMove> shiftBreakChangeMoves= new ArrayList<>();
        for(ShiftBreak shiftBreak:workingSolution.getShiftBreaks()){
            if(!date.equals(shiftBreak.getShift().getDate())){
                continue;
            }
            Interval possibleBreakInterval= ShiftPlanningUtility.getPossibleBreakStartInterval(shiftBreak,shiftBreak.getShift());
            for(DateTime dateTime:workingSolution.getPossibleStartDateTimes()){
                if(ShiftPlanningUtility.intervalConstainsTimeIncludingEnd(possibleBreakInterval,dateTime) && !dateTime.equals(shiftBreak.getStartTime())){
                    shiftBreakChangeMoves.add(new ShiftBreakChangeMove(shiftBreak,dateTime,
                            ShiftPlanningUtility.getOverlappingActivityLineIntervalsWithInterval(shiftBreak.getShift(),new Interval(dateTime,dateTime.plusMinutes(shiftBreak.getDuration()))),null));
                }
            }
        }
        Collections.shuffle(shiftBreakChangeMoves);
        return new ShiftBreakChangeMoveIterator(shiftBreakChangeMoves);
    }
}
