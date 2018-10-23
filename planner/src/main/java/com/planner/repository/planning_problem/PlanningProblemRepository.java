package com.planner.repository.planning_problem;

import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.repository.common.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanningProblemRepository extends MongoBaseRepository<PlanningProblem,String>{
}
