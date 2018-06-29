package com.kairos.planning.domain;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.kairos.planning.solution.TaskPlanningSolution;

public class MovableTaskSelectionFilter implements SelectionFilter<TaskPlanningSolution, Task>{

	@Override
	public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, Task selection) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
