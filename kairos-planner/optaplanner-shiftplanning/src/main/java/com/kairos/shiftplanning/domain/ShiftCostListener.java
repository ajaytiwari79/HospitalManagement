package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.math.BigDecimal;

public class ShiftCostListener implements VariableListener<ShiftRequestPhase> {
    @Override
    public boolean requiresUniqueEntityEvents() {
        return false;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ShiftRequestPhase shift) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ShiftRequestPhase shift) {
        updateCost(scoreDirector,shift);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ShiftRequestPhase shift) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ShiftRequestPhase shift) {
        updateCost(scoreDirector,shift);

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ShiftRequestPhase shift) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ShiftRequestPhase shift) {

    }

    public void updateCost(ScoreDirector scoreDirector,ShiftRequestPhase shift){
        //BigDecimal employeeCost = ShiftPlanningUtility.calculateCostOfEmployee(shift);
        scoreDirector.beforeVariableChanged(shift,"costOfEmployee");
        //shift.setCompensationValue(employeeCost);
        scoreDirector.afterVariableChanged(shift,"costOfEmployee");
    }
}
