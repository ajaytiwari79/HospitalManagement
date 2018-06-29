package com.kairos.shiftplanning.config;

import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import org.optaplanner.core.impl.phase.custom.AbstractCustomPhaseCommand;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class CustomPhase extends AbstractCustomPhaseCommand<ShiftRequestPhasePlanningSolution>{

	@Override
	public void changeWorkingSolution(ScoreDirector<ShiftRequestPhasePlanningSolution> scoreDirector) {
		/*ShiftPlanningSolution unsolved = scoreDirector.getWorkingSolution();
		for (int i=0;i<unsolved.getEmployees().size();i++) {
			unsolved.getEmployees().get(i).setVehicle(unsolved.getVehicleList().get(ThreadLocalRandom.current().nextInt(0, unsolved.getVehicleList().size()-1)));
		}*/
	}

}
