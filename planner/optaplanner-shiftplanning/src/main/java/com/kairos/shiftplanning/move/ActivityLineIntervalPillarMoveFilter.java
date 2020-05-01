package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActivityLineIntervalPillarMoveFilter implements SelectionFilter<ShiftRequestPhasePlanningSolution,PillarChangeMove<ShiftRequestPhasePlanningSolution>> {
    @Override
    public boolean accept(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector, PillarChangeMove<ShiftRequestPhasePlanningSolution> selection) {
        List objects = selection.getPillar();
        List<ActivityLineInterval> activityLineIntervalList=(List<ActivityLineInterval>)objects;
        Set<LocalDate> dates=new HashSet<>();
        Set<ZonedDateTime> dateTimes=new HashSet<>();
        ShiftImp shift=(ShiftImp)selection.getToPlanningValue();
        if(shift==null){
            return true;
        }
        for (ActivityLineInterval activityLineInterval:activityLineIntervalList) {
                dates.add(activityLineInterval.getStart().toLocalDate());
            dateTimes.add(activityLineInterval.getStart());
                if(dates.size()>0 || dateTimes.size()>0 || !activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                    return false;
                }
        }
        return true;
    }
}
