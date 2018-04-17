package com.kairos.planning.domain;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class LocationVariableUpdaterListener implements VariableListener<Task>{

	@Override
	public void beforeEntityAdded(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterEntityAdded(ScoreDirector scoreDirector, Task entity) {
		updateLocation(scoreDirector,entity);

	}

	private void updateLocation(ScoreDirector scoreDirector, Task task) {
		scoreDirector.beforeVariableChanged(task,"employee");
		//if(task.getEmployee()!=null)task.getEmployee().setLocation(task.getLocation());
		scoreDirector.afterVariableChanged(task,"employee");
	}

	@Override
	public void beforeVariableChanged(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterVariableChanged(ScoreDirector scoreDirector, Task entity) {
		updateLocation(scoreDirector,entity);

	}

	@Override
	public void beforeEntityRemoved(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterEntityRemoved(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub

	}

}
