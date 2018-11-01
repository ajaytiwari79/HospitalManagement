package com.kairos.shiftplanning.domain.move;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityLineIntervalPillarMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,PillarChangeMove<ShiftRequestPhasePlanningSolution>> {
    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, PillarChangeMove<ShiftRequestPhasePlanningSolution> selection) {
        List objects = selection.getPillar();
        List<ActivityLineInterval> activityLineIntervalList=(List<ActivityLineInterval>)objects;
        Set<LocalDate> dates=new HashSet<>();
        Set<DateTime> dateTimes=new HashSet<>();
        ShiftRequestPhase shift=(ShiftRequestPhase)selection.getToPlanningValue();
        if(shift==null){
            //If all the entities are being assigned to null then this might fix some broken constraint. So it's fine.
            return true;
        }
        for (ActivityLineInterval activityLineInterval:activityLineIntervalList) {
                dates.add(activityLineInterval.getStart().toLocalDate());
            dateTimes.add(activityLineInterval.getStart());
                if(dates.size()>0 || dateTimes.size()>0 || !activityLineInterval.getStart().toLocalDate().equals(shift.getDate())){
                    return false;
                }
        }

        return true;
    }
}
