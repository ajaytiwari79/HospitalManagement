package com.kairos.planning.domain;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.kairos.planning.solution.TaskPlanningSolution;

public class NearBySelectionFilter implements SelectionFilter<TaskPlanningSolution,ChangeMove> {
	
	@Override
	public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, ChangeMove changeMove) {
		 Task task = (Task)changeMove.getEntity();
		 TaskOrEmployee prevTaskOrEmployee = (TaskOrEmployee)changeMove.getToPlanningValue();
		 if(prevTaskOrEmployee instanceof Task){
			 Task prevTask = (Task) prevTaskOrEmployee;
			 if(task.isAfter(prevTask)) return true;
		 }
		return false;
	}

}
