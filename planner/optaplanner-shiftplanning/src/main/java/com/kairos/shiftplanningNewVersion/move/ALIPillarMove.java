package com.kairos.shiftplanningNewVersion.move;


import com.kairos.shiftplanningNewVersion.move.helper.ALIChangeMoveHelper;
import com.kairos.shiftplanningNewVersion.move.helper.ALIWrapper;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ALIPillarMove extends AbstractMove<StaffingLevelSolution> {
    private List<ALIWrapper> ALIWrappers;
    private List<ALIWrapper> exALIWrappers;
    private static Logger log= LoggerFactory.getLogger(ALIPillarMove.class);

    public ALIPillarMove(List<ALIWrapper> ALIWrappers, List<ALIWrapper> exALIWrappers) {
        this.ALIWrappers = ALIWrappers;
        this.exALIWrappers = exALIWrappers;
    }

    /**
     * It reassigns existing shifts ie ali's are assigned back to shifts they already had be the case of alis had different shifts.
     * @param scoreDirector
     * @return
     */
    @Override
    protected AbstractMove<StaffingLevelSolution> createUndoMove(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        List<ALIWrapper> undoALIWrappers = new ArrayList<>();
        for(ALIWrapper aliw: ALIWrappers){
            undoALIWrappers.add(new ALIWrapper(aliw.getActivityLineInterval(), aliw.getActivityLineInterval().getShift()));
        }
        List<ALIWrapper> exActivityLineIntervalsForThisShift= new ArrayList<>();
        for(ALIWrapper aliw: exALIWrappers){
            exActivityLineIntervalsForThisShift.add(new ALIWrapper(aliw.getActivityLineInterval(), aliw.getActivityLineInterval().getShift()));
        }
        return new ALIPillarMove(exActivityLineIntervalsForThisShift, undoALIWrappers);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        long start=System.currentTimeMillis();
        log.debug("*P1*********************STARTS********************");
        for(ALIWrapper aliw: exALIWrappers){
            ALIChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,aliw.getActivityLineInterval(),aliw.getShiftImp());
        }
        log.debug("*P1*********************ENDS********************");
        log.debug("*P2*********************STARTS********************");
        for(ALIWrapper aliw: ALIWrappers){
            ALIChangeMoveHelper.assignActivityIntervalToShift(scoreDirector,aliw.getActivityLineInterval(),aliw.getShiftImp());
        }
        log.debug("*P2*********************ENDS********************");

        //log.info("pillar application took:"+(System.currentTimeMillis()-start)/1000.0);
    }

    @Override
    public boolean isMoveDoable(ScoreDirector<StaffingLevelSolution> scoreDirector) {
        return  true;
    }

    @Override
    public Collection<?> getPlanningEntities() {
        return ALIWrappers.stream().map(a->a.getActivityLineInterval()).collect(Collectors.toList());
    }

    @Override
    public Collection<?> getPlanningValues() {
        return Collections.singletonList(ALIWrappers.get(0).getShiftImp());
    }

    @Override
    public ALIPillarMove rebase(ScoreDirector<StaffingLevelSolution> destinationScoreDirector) {
        List<ALIWrapper> updatedALIWrappers = new ArrayList<>();
        for (ALIWrapper ALIWrapper : this.ALIWrappers) {
            updatedALIWrappers.add(new ALIWrapper(destinationScoreDirector.lookUpWorkingObject(ALIWrapper.getActivityLineInterval()),destinationScoreDirector.lookUpWorkingObject(ALIWrapper.getShiftImp())));
        }
        List<ALIWrapper> updatedExALIWrappers = new ArrayList<>();
        for (ALIWrapper ALIWrapper : this.exALIWrappers) {
            updatedExALIWrappers.add(new ALIWrapper(destinationScoreDirector.lookUpWorkingObject(ALIWrapper.getActivityLineInterval()),destinationScoreDirector.lookUpWorkingObject(ALIWrapper.getShiftImp())));
        }
        return new ALIPillarMove(updatedALIWrappers, updatedExALIWrappers);
    }

    @Override
    public String toString() {
        boolean detailed=false;
        return "ActivityLineIntervalPillarMove{" +
                "activityLineIntervals=" + (detailed?(ALIWrappers.stream().map(a->a.getActivityLineInterval().getLabel()).collect(Collectors.toList())): ALIWrappers.size()) +" to:"+ ALIWrappers.get(0).getShiftImp()+
                '}';
    }
}
