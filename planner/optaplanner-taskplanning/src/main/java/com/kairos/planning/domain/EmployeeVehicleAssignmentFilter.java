package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

//TODO This is , as of now, for both CH and MH and should have separate for both as CH mostly have unitialized PVs
public class EmployeeVehicleAssignmentFilter implements SelectionFilter<TaskPlanningSolution,ChangeMove> {

    @Override
    public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, ChangeMove changeMove) {
        Vehicle vehicle = (Vehicle)changeMove.getEntity();
            Employee employee=(Employee)changeMove.getToPlanningValue();//task.getEmployee();


        return true;
    }
}
