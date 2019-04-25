package com.kairos.planning.domain;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BreakTimeListener implements VariableListener<Task>{
    public static Logger log= LoggerFactory.getLogger(BreakTimeListener.class);

	@Override
	public void beforeEntityAdded(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterEntityAdded(ScoreDirector scoreDirector, Task task) {
        addBreak(scoreDirector,task);

		
	}

	@Override
	public void beforeVariableChanged(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterVariableChanged(ScoreDirector scoreDirector, Task task) {
		addBreak(scoreDirector,task);

		
	}

	

	@Override
	public void beforeEntityRemoved(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterEntityRemoved(ScoreDirector scoreDirector, Task entity) {
		// TODO Auto-generated method stub
		
	}
	
	private void addBreak(ScoreDirector scoreDirector, Task task) {
		// TODO Auto-generated method stub
		
	}

}
