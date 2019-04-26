package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainChangeMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainReversingChangeMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskChainChangeMoveFilter implements SelectionFilter<TaskPlanningSolution,AbstractMove<TaskPlanningSolution>>{
	Logger log= LoggerFactory.getLogger(TaskChainChangeMoveFilter.class);

	@Override
	public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector, AbstractMove<TaskPlanningSolution> chainChangeMove) {
		final boolean[] movable = new boolean[]{true};
		if(chainChangeMove instanceof SubChainChangeMove){
			SubChainChangeMove<TaskPlanningSolution> subChainChangeMove= (SubChainChangeMove<TaskPlanningSolution>)chainChangeMove;
			Employee toBeEmployee= subChainChangeMove.getToPlanningValue() instanceof Employee?(Employee)subChainChangeMove.getToPlanningValue():
				((Task)subChainChangeMove.getToPlanningValue()).getEmployee();
			subChainChangeMove.getSubChain().getEntityList().forEach(task->{
				if(((Task)task).isLocked() &&  !((Task)task).getEmployee().getId().equals(toBeEmployee.getId())){
					movable[0]=false;
					return;
				}
			});
		} else if(chainChangeMove instanceof SubChainReversingChangeMove){
			SubChainReversingChangeMove<TaskPlanningSolution> subChainChangeMove= (SubChainReversingChangeMove<TaskPlanningSolution>)chainChangeMove;
			Employee toBeEmployee= subChainChangeMove.getToPlanningValue() instanceof Employee?(Employee)subChainChangeMove.getToPlanningValue():
				((Task)subChainChangeMove.getToPlanningValue()).getEmployee();
			subChainChangeMove.getSubChain().getEntityList().forEach(task->{
				if(((Task)task).isLocked() &&  !((Task)task).getEmployee().getId().equals(toBeEmployee.getId())){
					movable[0]=false;
					return;
				}
			});
		}else{
			log.error("IMPOSSIBLE");
		}
		return movable[0];
	}
}
