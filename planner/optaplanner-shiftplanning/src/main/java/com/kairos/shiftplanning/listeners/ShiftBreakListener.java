package com.kairos.shiftplanning.listeners;

import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.apache.commons.collections4.CollectionUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;
import java.util.Objects;

public class ShiftBreakListener implements VariableListener<Shift> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Shift shift) {
        //updateBreaks(scoreDirector,shift);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Shift shift) {
        //updateBreaks(scoreDirector,shift);

    }
    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Shift shift) {
        //Not in use
    }

    public void updateBreaks(ScoreDirector scoreDirector, Shift shift) {
        if(((ShiftImp)shift).isAbsenceActivityApplied()){
            return;
        }
        List<ShiftBreak> breaks= ShiftPlanningUtility.generateBreaksForShift(shift);
        if(!Objects.equals(shift.getBreaks(),breaks)){
            scoreDirector.beforeVariableChanged(shift,"breaks");
            shift.setBreaks(breaks);
            scoreDirector.afterVariableChanged(shift,"breaks");
            if(CollectionUtils.isNotEmpty(breaks)){
                //FIXME this triggers ali->shift which triggers shift->alis (IRSV) and then alis->start and start->breaks and break's overlapping ali-> null(shift) again making a loop
                //Also UNDO move wont undo breaks.. so it's more problematic than anything
               // ShiftPlanningUtility.unassignShiftIntervalsOverlappingBreaks(scoreDirector,(ShiftImp) shift,breaks);
            }

        }
    }
}
