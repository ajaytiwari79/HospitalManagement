package com.kairos.shiftplanning.listeners;

import com.kairos.shiftplanning.domain.shift.ShiftImp;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class ShiftCostListener implements VariableListener<ShiftImp> {
    @Override
    public boolean requiresUniqueEntityEvents() {
        return false;
    }

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, ShiftImp shift) {
        //Not in use
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, ShiftImp shift) {
        updateCost(scoreDirector,shift);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, ShiftImp shift) {
        //Not in use
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, ShiftImp shift) {
        updateCost(scoreDirector,shift);

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, ShiftImp shift) {
        //Not in use
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, ShiftImp shift) {
        //Not in use
    }

    public void updateCost(ScoreDirector scoreDirector, ShiftImp shift){
        //BigDecimal employeeCost = ShiftPlanningUtility.calculateCostOfEmployee(shift);
        scoreDirector.beforeVariableChanged(shift,"costOfEmployee");
        //shift.setCompensationValue(employeeCost);
        scoreDirector.afterVariableChanged(shift,"costOfEmployee");
    }
}
