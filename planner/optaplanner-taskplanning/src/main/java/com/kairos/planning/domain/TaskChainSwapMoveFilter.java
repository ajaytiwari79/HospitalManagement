package com.kairos.planning.domain;

import com.kairos.planning.solution.TaskPlanningSolution;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainReversingSwapMove;
import org.optaplanner.core.impl.heuristic.selector.move.generic.chained.SubChainSwapMove;
import org.optaplanner.core.impl.score.director.ScoreDirector;

public class TaskChainSwapMoveFilter implements SelectionFilter<TaskPlanningSolution,AbstractMove<TaskPlanningSolution>>{

	@Override
	public boolean accept(ScoreDirector<TaskPlanningSolution> scoreDirector,
			AbstractMove<TaskPlanningSolution> chainSwapMove) {
		final boolean[] movable = new boolean[]{true};
		if(chainSwapMove instanceof SubChainSwapMove){
			SubChainSwapMove<TaskPlanningSolution> subChainSwapMove= (SubChainSwapMove<TaskPlanningSolution>)chainSwapMove;
			subChainSwapMove.getLeftSubChain().getEntityList().forEach(task->{
				if(((Task)task).isLocked() && !((Task)task).getEmployee().getId().equals(((Task)subChainSwapMove.getRightSubChain().getFirstEntity()).getEmployee().getId())){
					movable[0]=false;
					return;
				}
			});
			if(movable[0]){
				subChainSwapMove.getRightSubChain().getEntityList().forEach(task->{
					if(((Task)task).isLocked() && !((Task)task).getEmployee().getId().equals(((Task)subChainSwapMove.getLeftSubChain().getFirstEntity()).getEmployee().getId())){
						movable[0]=false;
						return;
					}
				});
			}
		} else if(chainSwapMove instanceof SubChainReversingSwapMove){
			SubChainReversingSwapMove<TaskPlanningSolution> subChainSwapMove= (SubChainReversingSwapMove<TaskPlanningSolution>)chainSwapMove;
			subChainSwapMove.getLeftSubChain().getEntityList().forEach(task->{
				if(((Task)task).isLocked() && !((Task)task).getEmployee().getId().equals(((Task)subChainSwapMove.getRightSubChain().getFirstEntity()).getEmployee().getId())){
					movable[0]=false;
					return;
				}
			});
			if(movable[0]){
				subChainSwapMove.getRightSubChain().getEntityList().forEach(task->{
					if(((Task)task).isLocked() && !((Task)task).getEmployee().getId().equals(((Task)subChainSwapMove.getLeftSubChain().getFirstEntity()).getEmployee().getId())){
						movable[0]=false;
						return;
					}
				});
			}
		}
		return movable[0];
	}

}
