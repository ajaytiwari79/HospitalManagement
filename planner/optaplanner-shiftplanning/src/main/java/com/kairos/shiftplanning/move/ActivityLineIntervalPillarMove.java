package com.kairos.shiftplanning.move;

import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalChangeMoveHelper;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ActivityLineIntervalPillarMove extends AbstractMove<ShiftRequestPhasePlanningSolution> {
    private List<ActivityLineIntervalWrapper> activityLineIntervalWrappers;
    private List<ActivityLineIntervalWrapper> exActivityLineIntervalWrappers;
    private static Logger log= LoggerFactory.getLogger(ActivityLineIntervalPillarMove.class);

    public ActivityLineIntervalPillarMove(List<ActivityLineIntervalWrapper> activityLineIntervalWrappers,List<ActivityLineIntervalWrapper> exActivityLineIntervalWrappers) {
        this.activityLineIntervalWrappers =activityLineIntervalWrappers;
        this.exActivityLineIntervalWrappers =exActivityLineIntervalWrappers;
    }

    /**
     * It reassigns existing shifts ie ali's are assigned back to shifts they already had be the case of alis had different shifts.
     * @param scoreDirector
     * @return
     */
    @Override
    protected AbstractMove<ShiftRequestPhasePlanningSolution> createUndoMove(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        List<ActivityLineIntervalWrapper> undoActivityLineIntervalWrappers= new ArrayList<>();
        for(ActivityLineIntervalWrapper aliw: activityLineIntervalWrappers){
            undoActivityLineIntervalWrappers.add(new ActivityLineIntervalWrapper(aliw.getActivityLineInterval(), aliw.getActivityLineInterval().getShift()));
        }
        List<ActivityLineIntervalWrapper> exActivityLineIntervalsForThisShift= new ArrayList<>();
        for(ActivityLineIntervalWrapper aliw: exActivityLineIntervalWrappers){
            exActivityLineIntervalsForThisShift.add(new ActivityLineIntervalWrapper(aliw.getActivityLineInterval(), aliw.getActivityLineInterval().getShift()));
        }
        return new ActivityLineIntervalPillarMove(exActivityLineIntervalsForThisShift,undoActivityLineIntervalWrappers);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        long start=System.currentTimeMillis();
        log.debug("*P1*********************STARTS********************");
        for(ActivityLineIntervalWrapper aliw: exActivityLineIntervalWrappers){
            ActivityLineIntervalChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,aliw.getActivityLineInterval(),aliw.getShiftImp());
        }
        log.debug("*P1*********************ENDS********************");
        log.debug("*P2*********************STARTS********************");
        for(ActivityLineIntervalWrapper aliw: activityLineIntervalWrappers){
            ActivityLineIntervalChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,aliw.getActivityLineInterval(),aliw.getShiftImp());
        }
        log.debug("*P2*********************ENDS********************");

        //log.info("pillar application took:"+(System.currentTimeMillis()-start)/1000.0);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
        return  true;
        //return !Objects.equals(activityLineInterval.getShift(), toShift) && !ShiftPlanningUtility.intervalOverlapsBreak(toShift,activityLineInterval.getStart());
    }

    @Override
    public Collection<?> getPlanningEntities() {
        //return Collections.singletonList(activityLineInterval.stream().map(a->a.getActivityLineInterval()).collect(Collectors.toList()));
        return activityLineIntervalWrappers.stream().map(a->a.getActivityLineInterval()).collect(Collectors.toList());
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(activityLineIntervalWrappers.get(0).getShiftImp());
    }

    @Override
    public String toString() {
        boolean detailed=false;
        return "ActivityLineIntervalPillarMove{" +
                "activityLineIntervals=" + (detailed?(activityLineIntervalWrappers.stream().map(a->a.getActivityLineInterval().getLabel()).collect(Collectors.toList())):activityLineIntervalWrappers.size()) +" to:"+activityLineIntervalWrappers.get(0).getShiftImp()+
                '}';
    }
}
