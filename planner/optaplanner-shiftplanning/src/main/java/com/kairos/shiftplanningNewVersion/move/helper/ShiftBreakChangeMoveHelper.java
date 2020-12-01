package com.kairos.shiftplanningNewVersion.move.helper;


import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.ZonedDateTime;
import java.util.List;

public class ShiftBreakChangeMoveHelper {
    public static void assignShiftBreakToShift(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector, ShiftBreak shiftBreak,
                                               ZonedDateTime startTime, List<ALI> alis, Shift shift){
        /*log.debug("applying move:");
        for(ALI ali: alis){
            //scoreDirector.beforeVariableChanged(ali, "shift");
            ali.setShift(shift);
            //scoreDirector.afterVariableChanged(ali, "shift");
        }*/
        scoreDirector.beforeVariableChanged(shiftBreak, "startTime");
        shiftBreak.setStartTime(startTime);
        scoreDirector.afterVariableChanged(shiftBreak, "startTime");
    }
}
