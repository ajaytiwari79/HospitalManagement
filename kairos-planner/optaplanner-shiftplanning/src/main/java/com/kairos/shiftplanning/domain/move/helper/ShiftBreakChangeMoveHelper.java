package com.kairos.shiftplanning.domain.move.helper;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftBreak;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.domain.wta.ConstraintHandler;
import com.kairos.shiftplanning.solution.BreaksIndirectAndActivityPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.DateTime;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerNotifiable;
import org.optaplanner.core.impl.domain.variable.listener.support.VariableListenerSupport;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static com.kairos.shiftplanning.domain.wta.ConstraintHandler.log;

public class ShiftBreakChangeMoveHelper {
    public static Logger log= LoggerFactory.getLogger(ShiftBreakChangeMoveHelper.class);
    public static void assignShiftBreakToShift(ScoreDirector<BreaksIndirectAndActivityPlanningSolution> scoreDirector, ShiftBreak shiftBreak,
                                               DateTime startTime, List<ActivityLineInterval> alis, ShiftRequestPhase shift){
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
