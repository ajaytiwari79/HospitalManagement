package com.kairos.shiftplanning.move.helper;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ShiftBreakChangeMoveHelper {
    public static Logger log= LoggerFactory.getLogger(ShiftBreakChangeMoveHelper.class);
    public static void assignShiftBreakToShift(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector, ShiftBreak shiftBreak,
                                               DateTime startTime, List<ActivityLineInterval> alis, ShiftImp shift){
        /*log.debug("applying move:");
        for(ActivityLineInterval ali: alis){
            //scoreDirector.beforeVariableChanged(ali, "shift");
            ali.setShift(shift);
            //scoreDirector.afterVariableChanged(ali, "shift");
        }*/
        scoreDirector.beforeVariableChanged(shiftBreak, "startTime");
        shiftBreak.setStartTime(startTime);
        scoreDirector.afterVariableChanged(shiftBreak, "startTime");
    }
}
