package com.kairos.planning.config;

import java.util.concurrent.ThreadLocalRandom;

import org.optaplanner.core.impl.phase.custom.AbstractCustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import com.kairos.planning.solution.TaskPlanningSolution;

public class CustomPhase extends AbstractCustomPhaseCommand<TaskPlanningSolution>{

	@Override
	public void changeWorkingSolution(ScoreDirector<TaskPlanningSolution> scoreDirector) {
		TaskPlanningSolution unsolved = scoreDirector.getWorkingSolution();
		for (int i=0;i<unsolved.getEmployeeList().size();i++) {
			unsolved.getEmployeeList().get(i).setVehicle(unsolved.getVehicleList().get(ThreadLocalRandom.current().nextInt(0, unsolved.getVehicleList().size()-1)));
		}
	}

}
