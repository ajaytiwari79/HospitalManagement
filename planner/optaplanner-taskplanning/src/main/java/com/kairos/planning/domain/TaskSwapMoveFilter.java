package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.SwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class TaskSwapMoveFilter implements SelectionFilter<TaskPlanningSolution,SwapMove> {
   // @Override
    public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, SwapMove swapMove) {

            Task task1 = (Task)swapMove.getLeftEntity();
            Task task2 = (Task)swapMove.getRightEntity();
            Employee emp1= task1.getEmployee();
            Employee emp2= task2.getEmployee();
            if((task1.isLocked() || task2.isLocked()) && !task1.getEmployee().getId().equals(task2.getEmployee().getId())){
            	return false;
            }
           /* if(!emp1.canAttemptedToPlanIhisIntervals(task2.getPossibleStartIntervals())){
                return false;
            }
            if(!emp2.canAttemptedToPlanIhisIntervals(task1.getPossibleStartIntervals())){
                return false;
            }*/
            //TODO: re-factor to use TaskTimeWindow
            /*if(!TaskPlanningUtility.checkEmployeeAttemptedToPlanThisIntervals(emp1, task2.getPossibleStartIntervals(), scoreDirector)){
                return false;
            }
            if(!TaskPlanningUtility.checkEmployeeAttemptedToPlanThisIntervals(emp2,task1.getPossibleStartIntervals(),scoreDirector)){
                return false;
            }*/


        return true;
    }

}
