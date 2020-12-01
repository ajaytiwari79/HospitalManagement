package com.kairos.shiftplanningNewVersion.move;




import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.PillarChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ALIPillarMoveFilter implements SelectionFilter<StaffingLevelSolution,PillarChangeMove<StaffingLevelSolution>> {
    @Override
    public boolean accept(ScoreDirector<StaffingLevelSolution> scoreDirector, PillarChangeMove<StaffingLevelSolution> selection) {
        List objects = selection.getPillar();
        List<ALI> activityLineIntervalList=(List<ALI>)objects;
        Set<LocalDate> dates=new HashSet<>();
        Set<ZonedDateTime> dateTimes=new HashSet<>();
        Shift shift=(Shift)selection.getToPlanningValue();
        if(shift==null){
            return true;
        }
        for (ALI activityLineInterval:activityLineIntervalList) {
                dates.add(activityLineInterval.getStart().toLocalDate());
            dateTimes.add(activityLineInterval.getStart());
                if(dates.size()>0 || dateTimes.size()>0 || !activityLineInterval.getStart().toLocalDate().equals(shift.getStartDate())){
                    return false;
                }
        }
        return true;
    }
}
