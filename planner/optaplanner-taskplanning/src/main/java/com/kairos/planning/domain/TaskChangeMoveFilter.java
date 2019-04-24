package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskChangeMoveFilter implements SelectionFilter<TaskPlanningSolution,ChangeMove> {
	private static Logger log= LoggerFactory.getLogger(Task.class);

   // @Override
    public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, ChangeMove changeMove) {
             Task task = (Task)changeMove.getEntity();
            //prevTaskOrVehicle is the valve on which this filter works
            TaskOrEmployee toBePrevTaskOrEmployee = (TaskOrEmployee)changeMove.getToPlanningValue();
            Employee toBeEmployee= toBePrevTaskOrEmployee instanceof Employee?
            		(Employee)toBePrevTaskOrEmployee:((Task)toBePrevTaskOrEmployee).getEmployee();
            if(task.isLocked() && !toBeEmployee.getId().equals(task.getEmployee().getId())){
            	return false;
            }
            if(toBePrevTaskOrEmployee instanceof  Task){
                Task prevTask = (Task) toBePrevTaskOrEmployee;
                Employee employee=prevTask.getEmployee();//(Employee)changeMove.getToPlanningValue();//task.getEmployee();
                int missingSkills=prevTask.getMissingSkillCountForEmployee(employee);
                if(missingSkills>0){
                    return false;
                }
                /*if(!TaskPlanningUtility.checkEmployeeCanWorkThisInterval(employee, prevTask.getIntervalIncludingArrivalAndWaiting(), scoreDirector)){
                    return false;
                }
                if(prevTask.isLastTaskOfRoute() && !TaskPlanningUtility.checkEmployeeCanWorkThisInterval(employee,prevTask.getReachBackUnitInterval(),scoreDirector)){
                    return false;
                }*/
                /*if(!TaskPlanningUtility.checkEmployeeAttemptedToPlanThisIntervals(employee, task.getPossibleStartIntervals(), scoreDirector)){
                    return false;
                }*/
                /*if(!task.isAfterDependentTask()){
                    return false;
                }*/
            }


        return true;
    }

}
